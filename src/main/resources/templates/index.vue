<template>
  <div class="dashboard-editor-container">
    <el-form
      label-width="150px"
      size="medium"
      :inline="true"
      :model="eventRelationshipForm"
      class="form-container"
    >
      <el-row>
        <el-form-item label="messageTypes">
          <el-input v-model="eventRelationshipForm.messageTypes" style="width:220px"></el-input>
        </el-form-item>
        <el-form-item label="dateRange">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            placeholder="select date range"
            format="yyyy/MM/dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="dateTimePickerOptions"
            style="width:350px"
          ></el-date-picker>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="exceptionType">
          <el-select v-model="eventRelationshipForm.exceptionType" style="width:220px">
            <el-option value="BIZ_API_EXCEPTION"></el-option>
            <el-option value="RELEVANT_ASSET_RESULT_DEFECT_EXCEPTION"></el-option>
            <el-option value="UNKNOWN_EXCEPTION"></el-option>
            <el-option label="--" value></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="exceptionDetailMatch">
          <el-input v-model="eventRelationshipForm.exceptionDetailMatch" style="width:350px"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button :loading="searchLoading" type="primary" @click="search()">Search</el-button>
        </el-form-item>
        <el-form-item>
          <el-dropdown>
            <el-button :loading="reprocessBatchLoading" type="danger">
              Reprocess Batch
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="onReprocessBatch()">
                Reprocess Selected
                <span
                  v-show="messageIdBatch.length!==0"
                >({{messageIdBatch.length}})</span>
              </el-dropdown-item>
              <el-dropdown-item @click.native="onReprocessBatch('All')">
                Reprocess All
                <span
                  v-show="tableDataReprocessAll.length!==0"
                >({{tableDataReprocessAll.length}})</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="send()">Send</el-button>
        </el-form-item>
      </el-row>
    </el-form>
    <el-table
      :data="tableData.slice((pageNo - 1) * pageSize,pageNo* pageSize)"
      ref="dataTable"
      row-key="id"
      v-loading="searchLoading"
      v-if="tableData.length>0"
      style="width: 100%"
      show-overflow-tooltip
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" :selectable="handleSelectable" reserve-selection></el-table-column>
      <el-table-column type="expand">
        <template slot-scope="props">
          <div class="expand-container">
            <div class="queue-container">
              sourceQueue:{{props.row.sourceQueue}}
              <br />
              targetQueue:{{props.row.targetQueue}}
            </div>
            <div class="message-text-container">
              messageText:
              <pre v-if="props.row.messageText" style="white-space:pre-wrap;">{{props.row.messageText|formatString}}</pre>
            </div>
            <div class="exception-detail-container">
              exceptionDetail:
              <pre v-if="props.row.exceptionDetail" style="white-space:pre-wrap;">{{props.row.exceptionDetail|formatString}}</pre>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="messageId" label="messageId"></el-table-column>
      <el-table-column prop="exceptionType" label="exceptionType"></el-table-column>
      <el-table-column prop="messageType" label="messageType"></el-table-column>
      <el-table-column label="BR/BC/BL">
        <template
          v-if="scope.row.numberRelationship"
          slot-scope="scope"
        >{{scope.row.numberRelationship.csBookingRefNumber}}/{{scope.row.numberRelationship.bookingNumber}}/{{scope.row.numberRelationship.billOfLadingNumber}}</template>
      </el-table-column>
      <el-table-column label="updatedDate">
        <template slot-scope="scope">{{scope.row.updatedDate|formatTime}}</template>
      </el-table-column>
      <el-table-column prop="exceptionStatus" label="exceptionStatus"></el-table-column>
      <el-table-column label="Opration">
        <template slot-scope="scope">
          <el-button
            type="danger"
            @click="onReprocess(scope.row)"
            size="small"
            :disabled="scope.row.exceptionStatus!=='UNPROCESSED'"
            :loading="scope.row.reprocessLoading"
          >REPROCESS</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      class="pagination-container"
      background
      hide-on-single-page
      :total="tableData.length"
      :current-page="pageNo"
      @current-change="handleCurrentChange"
      layout="total, prev, pager, next"
    ></el-pagination>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from "vue-property-decorator";
import moment from "moment";
import {
  getExceptionMessage,
  reprocessMessage,
  reprocessMessageBatch
} from "@/api/exceptionMessage.ts";
@Component({
  filters: {
    formatString(str: string) {
      var beautify = require("js-beautify").js;
      return beautify(str.replace(/=/g, ":"), {
        indent_size: 2,
        space_in_empty_paren: true
      });
    },
    formatTime(time: string) {
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    }
  }
})
export default class DashboardAdmin extends Vue {
  dateRange: any = [];
  eventRelationshipForm: any = {
    messageTypes: "",
    startDate: "",
    endDate: "",
    exceptionDetailMatch: "",
    exceptionType: ""
  };
  eventRelationshipData: object = [];
  searchLoading: boolean = false;
  reprocessBatchLoading: boolean = false;
  reprocessAllLoading: boolean = false;
  messageIdBatch: any[] = [];
  tableData: any[] = [];
  pageNo: number = 1;
  pageSize: number = 10;
  dateTimePickerOptions: object = {
    disabledDate: (time: any) => {
      return !moment(time).isBetween(
        moment(new Date())
          .subtract(7, "days")
          .format("YYYY-MM-DD 00:00:00"),
        moment(new Date())
          .add(1, "days")
          .format("YYYY-MM-DD 00:00:00")
      );
    }
  };
  ws: any = new WebSocket("ws://127.0.0.1:8080/endPoint/panda");
  get tableDataReprocessAll() {
    return this.tableData.filter(
      (item: any) => item.exceptionStatus === "UNPROCESSED"
    );
  }

  mounted() {
    let yesterday = moment(new Date())
      .subtract(1, "days")
      .format("YYYY-MM-DD HH:mm:ss");
    let today = moment(new Date()).format("YYYY-MM-DD HH:mm:ss");
    this.dateRange = [yesterday, today];
    if (Object.keys(this.$route.query).length !== 0) {
      Object.assign(this.eventRelationshipForm, this.$route.query);
      this.dateRange[0] = this.eventRelationshipForm.startDate;
      this.dateRange[1] = this.eventRelationshipForm.endDate;
      this.search();
    }
    if (!("WebSocket" in window)) {
      alert("Not support websocket");
    }
    //连接成功建立的回调方法
    this.ws.onopen = function(event: any) {
      console.log("建立连接");
    };

    //接收到消息的回调方法
    this.ws.onmessage = function(event: any) {
      console.log("接收到内容：" + event.data);
      alert(event.data);
    };

    //连接发生错误的回调方法
    this.ws.onerror = function(event: any) {
      console.log("发生错误");
    };

    //连接关闭的回调方法
    this.ws.onclose = function(event: any) {
      console.log("关闭连接");
    };
    window.onbeforeunload = ()=> {
      this.ws.close();
    };
  }

  send() {
    this.ws.send(this.eventRelationshipForm.messageTypes);
  }

  async search() {
    this.ws.close();
    this.searchLoading = true;
    try {
      this.eventRelationshipForm.startDate = this.dateRange[0];
      this.eventRelationshipForm.endDate = this.dateRange[1];
      let res = await getExceptionMessage(this.eventRelationshipForm);
      this.tableData = res.data === "" ? [] : res.data;
      (this.$refs.dataTable as any).clearSelection();
      this.pageNo = 1;
    } finally {
      this.searchLoading = false;
    }
  }

  async onReprocess(row: any) {
    row.reprocessLoading = true;
    try {
      let res = await reprocessMessage(row.messageId);
      this.search();
      this.$message.success("Reprocess success !!");
    } catch {
      this.$message.error("Reprocess error !!");
    } finally {
      row.reprocessLoading = false;
    }
  }

  async onReprocessBatch(type?: string) {
    this.reprocessBatchLoading = true;
    const str =
      type === "All"
        ? this.tableDataReprocessAll.map((item: any) => item.messageId)
        : this.messageIdBatch.map((item: any) => item.messageId);
    try {
      let res = await reprocessMessageBatch(str.join(","));
      this.search();
      this.$message.success("Reprocess Batch success !!");
    } catch {
      this.$message.error("Reprocess Batch error !!");
    } finally {
      this.reprocessBatchLoading = false;
    }
  }

  handleCurrentChange(val: number) {
    this.pageNo = val;
  }

  handleSelectionChange(val: any) {
    this.messageIdBatch = val;
  }

  handleSelectable(row: any) {
    return row.exceptionStatus === "UNPROCESSED";
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;
  .github-corner {
    position: absolute;
    top: 0px;
    border: 0;
    right: 0;
  }
  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
  .expand-container {
    height: 500px;
    width: 100%;
    overflow: auto;
    .queue-container {
      padding: 20px 0 20px 0;
      border-bottom: 1px dashed rgb(180, 177, 177);
    }
    .message-text-container {
      float: left;
      width: 50%;
      overflow: auto;
      border-right: 1px dashed rgb(180, 177, 177);
    }
    .exception-detail-container {
      float: right;
      width: 50%;
      overflow: auto;
    }
  }
  .pagination-container {
    margin-top: 15px;
    text-align: center;
  }
  .form-container {
    width: fit-content;
  }
  .form-button-container {
    display: flex;
    justify-content: space-between;
  }
}
</style>

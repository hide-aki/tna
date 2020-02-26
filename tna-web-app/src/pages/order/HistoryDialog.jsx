import React, { Component } from "react";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import Table from "jazasoft/lib/mui/components/Table";

import moment from "moment";
import Button from "@material-ui/core/Button";
import { dataProvider } from "../../App";
import { RestMethods } from "jazasoft";

import { partialData, syntaxHighlight } from "../../utils/helpers";

const historyColumn = [
  { dataKey: "time", title: "Time" },
  { dataKey: "user", title: "User" },
  { dataKey: "event", title: "Event" },
  { dataKey: "data", title: "Data" }
];

class HistoryDialog extends Component {
  state = {
    dialogActive: false,
    dataStr: {},
    rows: {},
    logs: [],
    logId: null
  };

  componentDidMount() {
    const { data } = this.props;
    data[0] === "Order" ? this.fetchOrderHistory(data[1]) : this.fetchActivityHistory(data[1], data[2]);
  }

  fetchOrderHistory = id => {
    const options = {
      url: `orders/${id}/logs`,
      method: "GET"
    };
    try {
      dataProvider(RestMethods.CUSTOM, null, options).then(response => {
        if (response.status === 200 || response.status === 201) {
          let logs = response.data && response.data.map(e => e.data);
          this.setState({ logs });
          this.formatOrderHistory(response && response.data);
        }
      });
    } catch (err) {
      console.log(err);
    }
  };

  fetchActivityHistory = (orderId, activityId) => {
    const options = {
      url: `orders/${orderId}/activities/${activityId}/logs`,
      method: "GET"
    };
    try {
      dataProvider(RestMethods.CUSTOM, null, options).then(response => {
        if (response.status === 200 || response.status === 201) {
          console.log({ response });

          // let logs = response.data && response.data.map(e => e.data);
          // this.setState({ logs });
          // this.formatActivityHistory(response && response.data);
        }
      });
    } catch (err) {
      console.log(err);
    }
  };

  formatOrderHistory = data => {
    let rows =
      data &&
      data
        .sort((a, b) => a.timestamp - b.timestamp)
        .map(e => ({
          ...e,
          time: moment(e.timestamp).format("DD MMM, YY hh:mm a"),
          data: e.data ? partialData(e.data, this.onViewClick(e)) : "-"
        }));
    this.setState({ rows });
  };

  onViewClick = log => e => {
    e.preventDefault();
    let dataStr;
    if (log) {
      if (log.diff) {
        dataStr = log.diff;
      } else {
        dataStr = syntaxHighlight(JSON.stringify(JSON.parse(log.data), null, 4));
      }
    }
    this.setState({ dialogActive: true, dataStr });
  };

  onClose = () => {
    this.props.onClose && this.props.onClose();
  };

  render() {
    const { dataStr, dialogActive, rows } = this.state;

    return (
      <React.Fragment>
        <Dialog open={this.props.open} maxWidth="md" fullWidth onClose={this.onClose} aria-labelledby="form-dialog-title">
          <DialogTitle id="form-dialog-title">History</DialogTitle>
          <DialogContent style={{ padding: 0 }}>
            <Table columns={historyColumn} rows={rows} />
          </DialogContent>
          <DialogActions>
            <Button onClick={this.onClose} color="primary">
              Close
            </Button>
          </DialogActions>
        </Dialog>
        <Dialog open={dialogActive} maxWidth="sm" fullWidth onClose={_ => this.setState({ dialogActive: false })} aria-labelledby="form-dialog-title">
          <DialogContent style={{ padding: 0 }}>
            <span style={{ whiteSpace: "pre", fontSize: 14 }} dangerouslySetInnerHTML={{ __html: dataStr }} />
          </DialogContent>
          <DialogActions>
            <Button onClick={_ => this.setState({ dialogActive: false })} color="primary">
              Close
            </Button>
          </DialogActions>
        </Dialog>
      </React.Fragment>
    );
  }
}

export default HistoryDialog;

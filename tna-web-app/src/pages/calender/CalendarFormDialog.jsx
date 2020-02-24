import React, { Component } from "react";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import Table from "jazasoft/lib/mui/components/Table";

import moment from "moment";
import Button from "@material-ui/core/Button";

const columns = [
  { dataKey: "name", title: "Name" },
  { dataKey: "dueDate", title: "Due Date" },
  { dataKey: "completedDate", title: "Completed Date" },
  { dataKey: "poRef", title: "PO Ref. No." },
  { dataKey: "buyer", title: "Buyer" },
  { dataKey: "season", title: "Season" },
  { dataKey: "style", title: "Style" },
  { dataKey: "orderQty", title: "Order Qty" }
];

class CalendarFormDialog extends Component {
  state = {};

  onBack = () => {
    this.props.onClose && this.props.onClose();
  };

  render() {
    const { data } = this.props;
    let rows = [];
    if (data.length) {
      // Checking if data is from month view or week view
      let monthData =
        data &&
        data.map(e => ({
          ...e,
          dueDate: moment(e.dueDate).format("ll"),
          completedDate: e.completedDate ? moment(e.completedDate).format("ll") : "-"
        }));
      rows = monthData;
    } else {
      let weekData = [
        {
          ...data,
          name: data.title,
          dueDate: moment(data.dueDate).format("ll"),
          completedDate: data.completedDate ? moment(data.completedDate).format("ll") : "-"
        }
      ];
      rows = weekData;
    }
    return (
      <Dialog open={this.props.open} maxWidth="md" fullWidth onClose={this.handleClose} aria-labelledby="form-dialog-title">
        <DialogTitle id="form-dialog-title">Task</DialogTitle>
        <DialogContent style={{ padding: 0 }}>
          <Table columns={columns} rows={rows} />
        </DialogContent>
        <DialogActions>
          <Button onClick={this.onBack} color="primary">
            Close
          </Button>
        </DialogActions>
      </Dialog>
    );
  }
}

export default CalendarFormDialog;

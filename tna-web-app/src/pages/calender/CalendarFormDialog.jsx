import React, { Component } from "react";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import Table from "jazasoft/lib/mui/components/Table";
import Grid from "@material-ui/core/Grid";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import ListItemText from "@material-ui/core/ListItemText";
import Typography from "@material-ui/core/Typography";

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

  onClose = () => {
    this.props.onClose && this.props.onClose();
  };

  render() {
    const { data } = this.props;
    let listData = [];
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
      let weekAndDayData = [
        { label: "Name", value: data.name },
        { label: "Due Date", value: moment(data.dueDate).format("ll") },
        { label: "Completed Date", value: data.completedDate ? moment(data.completedDate).format("ll") : "-" },
        { label: "PO Ref. No", value: data.poRef ? data.poRef : "-" },
        { label: "Buyer", value: data.buyerName ? data.buyerName : "-" },
        { label: "Season", value: data.seasonName ? data.seasonName : "-" },
        { label: "Style", value: data.style ? data.style : "-" },
        { label: "OrderQty", value: data.orderQty ? data.orderQty : "-" }
      ];
      listData = weekAndDayData;
    }

    return (
      <Dialog open={this.props.open} maxWidth={data.length ? "md" : "xs"} fullWidth onClose={this.onClose} aria-labelledby="form-dialog-title">
        <DialogTitle id="form-dialog-title">Task</DialogTitle>
        <DialogContent style={{ padding: 0 }}>
          {data.length ? (
            <Table columns={columns} rows={rows} />
          ) : (
            <Grid item xs={12} sm={8}>
              <List>
                {listData.length > 0 &&
                  listData.map(e => (
                    <ListItem key={e.label}>
                      <ListItemText secondary={e.label} secondaryTypographyProps={{ variant: "subtitle1" }} />
                      <ListItemSecondaryAction>
                        <Typography component="div">{e.value}</Typography>
                      </ListItemSecondaryAction>
                    </ListItem>
                  ))}
              </List>
            </Grid>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={this.onClose} color="primary">
            Close
          </Button>
        </DialogActions>
      </Dialog>
    );
  }
}

export default CalendarFormDialog;
import React, { Component } from "react";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";
import Table, { TextInput } from "jazasoft/lib/mui/components/Table";

import isEqual from "lodash/isEqual";

import Button from "@material-ui/core/Button";

const overridableColumn = onChange => [
  { dataKey: "name", title: "Name" },
  { dataKey: "finalLeadTime", title: "Lead Time", element: <TextInput type="number" onChange={onChange} /> }
];

class OverridableFormDialog extends Component {
  state = {
    order: {},
    nonOverridableActivityList: [],
    overridableActivityList: []
  };

  componentDidMount() {
    this.init();
  }

  //WARNING! To be deprecated in React v17. Use new lifecycle static getDerivedStateFromProps instead.
  componentWillReceiveProps(nextProps) {
    if (!isEqual(this.props.data, nextProps.data) || (nextProps.open && !this.props.open)) {
      this.init(nextProps);
    }
  }

  init = (props = this.props) => {
    const { data = {} } = props;
    const overridableActivityList = [];
    const nonOverridableActivityList = [];
    data &&
      data.oActivityList &&
      data.oActivityList.map(e => (e.tActivity.overridable ? overridableActivityList.push(e) : nonOverridableActivityList.push(e)));
    overridableActivityList.sort((a, b) => a.tActivity.serialNo - b.tActivity.serialNo);
    this.setState({ overridableActivityList, nonOverridableActivityList, order: data });
  };

  onChange = ({ rowIdx, column }) => e => {
    let overridableActivityList = this.state.overridableActivityList.slice();
    if (column.dataKey === "finalLeadTime") {
      overridableActivityList[rowIdx][column.dataKey] = Number(e.target.value);
    }
    this.setState({ overridableActivityList });
  };

  onBack = () => {
    this.props.onClose && this.props.onClose();
  };

  onSubmit = () => {
    const { order, overridableActivityList, nonOverridableActivityList } = this.state;
    const oActivityList = [...overridableActivityList, ...nonOverridableActivityList];
    const values = {
      ...order,
      oActivityList
    };
    this.props.onSubmit && this.props.onSubmit(values)
  };

  render() {
    return (
      <Dialog open={this.props.open} maxWidth="md" fullWidth onClose={this.handleClose} aria-labelledby="form-dialog-title">
        <DialogTitle id="form-dialog-title">Activities</DialogTitle>
        <DialogContent style={{ padding: 0 }}>
          <Table columns={overridableColumn(this.onChange)} rows={this.state.overridableActivityList} />
        </DialogContent>
        <DialogActions>
          <Button onClick={this.onBack} color="primary">
            Close
          </Button>
          <Button onClick={this.onSubmit} color="primary">
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    );
  }
}

export default OverridableFormDialog;

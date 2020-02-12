import React from "react";
import isEqual from "lodash/isEqual";

import Button from "@material-ui/core/Button";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogTitle from "@material-ui/core/DialogTitle";

import Table, { TextInput } from "jazasoft/lib/mui/components/Table";
import { SelectInput, DateInput, LinkField } from "../../components/Table";
import { getDistinctValues } from "../../utils/helpers";

const columnsActivity = (onClick, onChange) => [
  { dataKey: "activity", title: "Activity", element: <LinkField color="#6d9dc7" onClick={onClick} /> },
  { dataKey: "completedDate", title: "Completed Date", element: <DateInput onChange={onChange} /> },
  {
    dataKey: "delayReason",
    title: "Delay Reason",
    element: (
      <SelectInput
        width={250}
        multiple={true}
        onChange={onChange}
        choices={({ record = {} }) => (record.delayReasons ? record.delayReasons.split(",").map(e => ({ id: e.trim(), name: e.trim() })) : [])}
      />
    )
  },
  { dataKey: "remarks", title: "Remarks", element: <TextInput width={250} onChange={onChange} /> }
];

const columnsSubActivity = onChange => [
  { dataKey: "subActivity", title: "Sub Activity" },
  { dataKey: "completedDate", title: "Completed Date", element: <DateInput onChange={onChange} /> },
  { dataKey: "remarks", title: "Remarks", element: <TextInput width={400} onChange={onChange} /> }
];

class FormDialog extends React.Component {
  state = {
    page: "Activity",
    activity: undefined,
    rowsActivity: [],
    rowsSubActivity: []
  };

  componentDidMount() {
    this.init();
  }

  componentWillReceiveProps(nextProps) {
    if (!isEqual(this.props.data, nextProps.data) || !isEqual(this.props.ids, nextProps.ids)) {
      this.init(nextProps);
    }
  }

  init = (props = this.props) => {
    const { data = {}, ids = [] } = props;

    const orderList = ids.map(id => data[id]).filter(e => e);
    if (orderList.length === 0) return;
    // find common activities
    const aIds = getDistinctValues(orderList.flatMap(e => (e.oActivityList ? e.oActivityList : [])).map(a => btoa(a.name)));

    let commonActivityIds = [];
    for (let i = 0; i < aIds.length; i++) {
      if (orderList.length === orderList.filter(e => e.oActivityList && e.oActivityList.map(oa => btoa(oa.name)).includes(aIds[i])).length) {
        commonActivityIds.push(aIds[i]);
      }
    }

    const activityList = orderList[0].oActivityList ? orderList[0].oActivityList.filter(oa => commonActivityIds.includes(btoa(oa.name))) : [];
    const rowsActivity = activityList
      .map(({ id, tActivity, ...a }) => ({
        ...a,
        serialNo: tActivity.serialNo,
        delayReasons: tActivity.delayReasons,
        delayReason: a.delayReason ? a.delayReason.split(",").map(e => e.trim()) : [],
        activityId: btoa(a.name),
        activity: a.name
      }))
      .sort((a, b) => a.serialNo - b.serialNo);
    this.setState({ rowsActivity });
  };

  handleSubmit = () => {
    const { page, rowsActivity, rowsSubActivity, activity } = this.state;
    if (page === "Activity") {
      this.props.onSubmit && this.props.onSubmit(page, rowsActivity);
    } else if (page === "SubActivity") {
      this.props.onSubmit && this.props.onSubmit(page, rowsSubActivity, activity);
    }
  };

  onChange = ({ rowIdx, column }) => e => {
    if (this.state.page === "Activity") {
      let rowsActivity = this.state.rowsActivity.slice();
      if (column.dataKey === "completedDate") {
        rowsActivity[rowIdx][column.dataKey] = e;
      } else {
        rowsActivity[rowIdx][column.dataKey] = e.target.value;
      }
      this.setState({ rowsActivity });
    } else if (this.state.page === "SubActivity") {
      let rowsSubActivity = this.state.rowsSubActivity.slice();
      if (column.dataKey === "completedDate") {
        rowsSubActivity[rowIdx][column.dataKey] = e;
      } else {
        rowsSubActivity[rowIdx][column.dataKey] = e.target.value;
      }
      this.setState({ rowsSubActivity });
    }
  };

  onClick = ({ rowIdx }) => e => {
    const activity = this.state.rowsActivity[rowIdx];

    const { data = {}, ids = [] } = this.props;
    const orderList = ids.map(id => data[id]).filter(e => e);

    const saIds = getDistinctValues(
      orderList
        .flatMap(e => {
          const oActivity = e.oActivityList && e.oActivityList.find(a => a.name === activity.name);
          return oActivity && oActivity.oSubActivityList ? oActivity.oSubActivityList : [];
        })
        .map(sa => btoa(sa.name))
    );

    let commonSubActivityIds = [];
    for (let i = 0; i < saIds.length; i++) {
      const count = orderList.filter(e => {
        const oActivity = e.oActivityList && e.oActivityList.find(a => a.name === activity.name);
        return oActivity && oActivity.oSubActivityList && oActivity.oSubActivityList.map(osa => btoa(osa.name).includes(saIds[i]));
      }).length;

      if (orderList.length === count) {
        commonSubActivityIds.push(saIds[i]);
      }
    }

    const subActivityList = activity.oSubActivityList.filter(soa => commonSubActivityIds.includes(btoa(soa.name)));
    const rowsSubActivity = subActivityList
      .map(({ id, ...sa }) => ({ ...sa, subActivityId: btoa(sa.name), subActivity: sa.name }))
      .sort((a, b) => a.serialNo - b.serialNo);
    this.setState({ activity, rowsSubActivity, page: "SubActivity" });
  };

  handleClose = () => {
    this.setState({
      page: "Activity",
      activity: undefined,
      rowsActivity: [],
      rowsSubActivity: []
    });
    this.props.onClose && this.props.onClose();
  };

  onBack = () => {
    this.setState({
      page: "Activity",
      activity: undefined,
      rowsSubActivity: []
    });
  };

  render() {
    const { page, rowsActivity, rowsSubActivity, activity = {} } = this.state;

    return (
      <Dialog open={this.props.open} maxWidth="md" fullWidth onClose={this.handleClose} aria-labelledby="form-dialog-title">
        <DialogTitle id="form-dialog-title">{page === "Activity" ? "Activities" : `${activity.name} --> Sub Activities`}</DialogTitle>
        <DialogContent style={{ padding: 0 }}>
          <Table
            columns={page === "Activity" ? columnsActivity(this.onClick, this.onChange) : columnsSubActivity(this.onChange)}
            rows={page === "Activity" ? rowsActivity : rowsSubActivity}
          />
        </DialogContent>
        <DialogActions>
          {page === "SubActivity" && (
            <Button onClick={this.onBack} color="primary">
              Back
            </Button>
          )}
          <Button onClick={this.handleClose} color="primary">
            Close
          </Button>
          <Button onClick={this.handleSubmit} color="primary">
            Submit
          </Button>
        </DialogActions>
      </Dialog>
    );
  }
}

export default FormDialog;

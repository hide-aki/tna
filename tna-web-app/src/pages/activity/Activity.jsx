import React, { Component } from "react";
import { withStyles } from "@material-ui/styles";
import { connect } from "react-redux";
import isEqual from "lodash/isEqual";

//icons
import AddIcon from "@material-ui/icons/Add";
import EditIcon from "@material-ui/icons/Create";
import ViewIcon from "@material-ui/icons/Visibility";
import ArrowUp from "@material-ui/icons/ArrowUpward";
import ArrowDown from "@material-ui/icons/ArrowDownward";

import Table from "jazasoft/lib/mui/components/Table";
import MuiButton from "@material-ui/core/Button";
import { Paper, Typography } from "@material-ui/core";

import { Button, crudGetList, RestMethods, FETCH_START, FETCH_END, SAVING_START, SAVING_END } from "jazasoft";

import { dataProvider } from "../../App";

const homeStyle = theme => ({
  root: {
    margin: "1.5em"
  },
  button: {
    width: theme.spacing(2)
  },
  viewEditBtn: {
    marginRight: "2em"
  },
  header: {
    margin: "1em 1.5em",
    display: "flex",
    flexDirection: "row",
    alignItems: "center"
  },
  headerCell: {
    padding: "16px",
    fontSize: 14,
    "&:last-child": {
      padding: "16px",
      fontSize: 14
    }
  },
  rowCell: {
    padding: "8px 16px",
    fontSize: 14,
    "&:last-child": {
      padding: "8px 16px",
      fontSize: 14
    }
  },
  displayCenter: {
    display: "flex",
    justifyContent: "center"
  }
});

class Activity extends Component {
  state = {
    editing: false,
    activityList: []
  };

  componentDidMount() {
    this.init();
    this.props.dispatch(crudGetList("activities"));
  }

  componentWillReceiveProps(nextProps) {
    if (!isEqual(this.props.activities, nextProps.activities)) {
      this.init(nextProps);
    }
  }

  init = (props = this.props) => {
    const { activities } = props;
    const activityList = Object.keys(activities)
      .map(id => activities[id])
      .sort((a, b) => a.serialNo - b.serialNo);
    this.setState({ activityList });
  };

  onSubmit = () => {
    const { activityList } = this.state;
    const data = activityList.map((activity, idx) => ({ ...activity, serialNo: idx + 1 }));

    const options = {
      url: "activities",
      method: "put",
      data
    };
    this.props.dispatch({ type: FETCH_START });
    this.props.dispatch({ type: SAVING_START });
    dataProvider(RestMethods.CUSTOM, null, options)
      .then(response => {
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
        this.props.dispatch(crudGetList("activities"));
        this.setState({ editing: false });
        // this.setState({activityList: response.data});
      })
      .catch(error => {
        console.log(error);
        this.props.dispatch({ type: FETCH_END });
        this.props.dispatch({ type: SAVING_END });
      });
  };

  getColumns = () => {
    if (this.state.editing) {
      return [
        { dataKey: "action", title: "" },
        { dataKey: "name", title: "Name" },
        { dataKey: "department", title: "Department" }
      ];
    } else {
      return [
        { dataKey: "name", title: "Name" },
        { dataKey: "department", title: "Department" },
        { dataKey: "action", title: "" }
      ];
    }
  };

  onActionClick = (name, idx) => e => {
    let activityList = this.state.activityList.slice();
    if (name === "up") {
      const el = activityList.splice(idx, 1);
      activityList.splice(idx - 1, 0, el[0]);
    } else if (name === "down") {
      const el = activityList.splice(idx, 1);
      activityList.splice(idx + 1, 0, el[0]);
    }
    this.setState({ activityList });
  };

  render() {
    const { classes, saving } = this.props;
    const { activityList, editing } = this.state;
    const rows = activityList.map((activity, idx) => ({
      ...activity,
      department: activity.department.name,
      action: editing ? (
        <div style={{ width: 65 }}>
          {idx !== 0 && <ArrowUp onClick={this.onActionClick("up", idx)} />}
          {idx !== activityList.length - 1 && <ArrowDown onClick={this.onActionClick("down", idx)} />}
        </div>
      ) : (
        <div style={{ display: "flex", justifyContent: "flex-end" }}>
          <Button label="View" className={classes.viewEditBtn} onClick={() => this.props.history.push(`/activities/${activity.id}/view`)}>
            <ViewIcon />
          </Button>
          <Button label="Edit" className={classes.viewEditBtn} onClick={() => this.props.history.push(`/activities/${activity.id}/edit`)}>
            <EditIcon />
          </Button>
        </div>
      )
    }));

    return (
      <div className={classes.root}>
        {/* Header */}
        <div className={classes.header}>
          <div>
            <Typography variant="h5">Activity</Typography>
          </div>
          <div style={{ flexGrow: 1 }} />
          <div style={{ marginLeft: "1em" }}>
            {("activity", "create") && (
              <Button showLabel={false} label="Create" onClick={() => this.props.history.push("/activities/create")}>
                <AddIcon />
              </Button>
            )}
          </div>
        </div>

        {/* MUI Table */}
        <Paper square style={{ padding: activityList.length === 0 ? "0.75em" : 0 }}>
          <Table classes={{ headerCell: classes.headerCell, rowCell: classes.rowCell }} columns={this.getColumns()} rows={rows} />
          {activityList.length > 0 && (
            <div
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "flex-end"
              }}
            >
              {editing ? (
                <React.Fragment>
                  <MuiButton color="primary" style={{ margin: "1em" }} onClick={_ => this.setState({ editing: false })}>
                    Cancel
                  </MuiButton>
                  <MuiButton
                    color={saving ? "default" : "primary"}
                    disabled={saving}
                    variant="contained"
                    style={{ margin: "1em" }}
                    onClick={this.onSubmit}
                  >
                    Save
                  </MuiButton>
                </React.Fragment>
              ) : (
                <MuiButton color="primary" style={{ margin: "1em" }} onClick={_ => this.setState({ editing: true })}>
                  Edit
                </MuiButton>
              )}
            </div>
          )}
        </Paper>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  activities: state.jazasoft.resources["activities"] && state.jazasoft.resources["activities"].data,
  saving: state.jazasoft.saving
});

export default connect(mapStateToProps)(withStyles(homeStyle)(Activity));

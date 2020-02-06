import React, { Component } from "react";
import { withStyles } from "@material-ui/styles";
import { connect } from "react-redux";
import { Button, crudGetList } from "jazasoft";

import Table from "jazasoft/lib/mui/components/Table";
import MuiButton from "@material-ui/core/Button";
import { Paper, Typography } from "@material-ui/core";
//icons
import AddIcon from "@material-ui/icons/Add";
import EditIcon from "@material-ui/icons/Create";
import ViewIcon from "@material-ui/icons/Visibility";

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
  displayCenter: {
    display: "flex",
    justifyContent: "center"
  }
});

const columns = [
  { dataKey: "name", title: "Name" },
  { dataKey: "department", title: "Department" },
  { dataKey: "action", title: "" }
];

class Activity extends Component {
 
  componentDidMount() {
    this.props.dispatch(crudGetList("activities"));
  }

  onClose = () => {
    this.setState({ dialogActive: false });
  };
  onSubmit = () => {
    console.log("Working onSubmit");
  };

  render() {
    const { activities, classes } = this.props;

    const activityList = activities
      ? Object.keys(activities).map(id => activities[id])
      : [];

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
              <Button
                showLabel={false}
                label="Create"
                onClick={() => this.props.history.push("/activities/create")}
              >
                <AddIcon />
              </Button>
            )}
          </div>
        </div>
        {/* MUI Table */}
        <Paper style={{ paddingLeft: "1em" }}>
          {activityList.length === 0 ? (
            <div className={classes.displayCenter}>
              <Typography color="primary">Data not available</Typography>{" "}
            </div>
          ) : (
            <div>
              <Table
                columns={columns}
                rows={activityList.map(activity => ({
                  ...activity,
                  department: activity.department.name,
                  action: (
                    <div
                      style={{ display: "flex", justifyContent: "flex-end" }}
                    >
                      <Button
                        label="View"
                        className={classes.viewEditBtn}
                        onClick={() =>
                          this.props.history.push(
                            `/activities/${activity.id}/view`
                          )
                        }
                      >
                        <ViewIcon />
                      </Button>
                      <Button
                        label="Edit"
                        className={classes.viewEditBtn}
                        onClick={() =>
                          this.props.history.push(
                            `/activities/${activity.id}/edit`
                          )
                        }
                      >
                        <EditIcon />
                      </Button>
                    </div>
                  )
                }))}
              />
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "flex-end"
                }}
              >
                <MuiButton
                  size="small"
                  color="primary"
                  style={{ margin: "0.5em" }}
                >
                  Edit
                </MuiButton>
              </div>
            </div>
          )}
        </Paper>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  activities:
    state.jazasoft.resources["activities"] &&
    state.jazasoft.resources["activities"].data,
  saving: state.jazasoft.saving
});

export default connect(mapStateToProps)(withStyles(homeStyle)(Activity));

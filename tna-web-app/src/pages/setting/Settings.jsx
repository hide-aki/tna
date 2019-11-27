import React, { Component } from "react";

import { withStyles } from "@material-ui/styles";

import ExpansionPanel from "@material-ui/core/ExpansionPanel";
import ExpansionPanelSummary from "@material-ui/core/ExpansionPanelSummary";
import ExpansionPanelDetails from "@material-ui/core/ExpansionPanelDetails";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Typography from "@material-ui/core/Typography";
import Divider from "@material-ui/core/Divider";

import MaterialTable from "material-table";

import SyncIcon from "@material-ui/icons/Sync";

import { PageHeader, Button } from "jazasoft";

import SettingsController from "./SettingsController";

const Actions = ({ onRefresh }) => (
  <div>
    <Button showLabel={false} label="Refresh" onClick={() => onRefresh && onRefresh("refresh")}>
      <SyncIcon />
    </Button>
  </div>
);

const columns = [
  { title: "Setting", field: "name" },
  { title: "Description", field: "description" },
  { title: "Value", field: "valueWithUnit" }
];

const styles = {
  root: {
    margin: "1.5em"
  }
};

class Settings extends Component {
  state = {
    expanded: "default"
  };

  renderTable = group => {
    const { settings } = this.props;
    return (
      <MaterialTable
        columns={columns}
        data={
          settings &&
          settings
            .filter(setting => !setting.hidden && setting.groupId === group.id)
            .sort((a, b) => a.serialNo - b.serialNo)
            .map(setting => ({
              ...setting,
              valueWithUnit:
                setting.dataType === "boolean"
                  ? setting.value === "true"
                    ? "Yes"
                    : "No"
                  : `${setting.value}${setting.unit}`
            }))
        }
        options={{
          toolbar: false,
          search: false,
          paging: false,
          header: true
        }}
        style={{
          boxShadow: "none"
        }}
      />
    );
  };

  render() {
    const { classes, groups } = this.props;

    return (
      <div>
        <PageHeader title="Settings">
          <Actions onRefresh={this.props.fetchSettings} />
        </PageHeader>

        <div className={classes.root}>
          {groups &&
            groups.length > 1 &&
            groups.map(group => (
              <ExpansionPanel
                key={group.id}
                expanded={this.state.expanded === group.id}
                onChange={(e, isExpanded) => this.setState({ expanded: isExpanded ? group.id : false })}
              >
                <ExpansionPanelSummary expandIcon={<ExpandMoreIcon />} id={group.id}>
                  <Typography className={classes.heading}>{group.name}</Typography>
                </ExpansionPanelSummary>
                <Divider />
                <ExpansionPanelDetails style={{ flexDirection: "column", padding: 0 }}>
                  {this.renderTable(group)}
                </ExpansionPanelDetails>
              </ExpansionPanel>
            ))}
          {groups && groups.length === 1 && this.renderTable(groups[0])}
        </div>
      </div>
    );
  }
}

const StyledSettings = withStyles(styles)(Settings);

export default props => (
  <SettingsController {...props}>
    {controllerProps => <StyledSettings {...props} {...controllerProps} />}
  </SettingsController>
);

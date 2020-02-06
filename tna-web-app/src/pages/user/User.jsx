import React from "react";
import { connect } from "react-redux";

import withStyles from "@material-ui/styles/withStyles";

import SyncIcon from "@material-ui/icons/Sync";
import UploadIcon from "mdi-material-ui/Upload";

import { crudGetList, List, Datagrid, TextField, CreateButton, EditButton, ShowButton, Button } from "jazasoft";

const Actions = ({ basePath, resource, filters, onSync, history, ...rest }) => (
  <div>
    <CreateButton basePath={basePath} showLabel={false} />
    <Button showLabel={false} label="Upload" onClick={() => history.push("/users/upload")}>
      <UploadIcon />
    </Button>
    <Button showLabel={false} label="Sync" onClick={onSync}>
      <SyncIcon />
    </Button>
  </div>
);

const styles = theme => ({
  button: {
    width: theme.spacing(1)
  }
});

class User extends React.Component {
  syncUsers = () => {
    this.props.dispatch(crudGetList(this.props.resource, null, null, null, null, { params: { action: "sync" } }));
  };

  render() {
    const { classes, dispatch, ...props } = this.props;
    return (
      <List
        searchKeys={["fullName", "username", "email", "roles"]}
        actions={<Actions history={this.props.history} onSync={this.syncUsers} />}
        {...props}
      >
        <Datagrid>
          <TextField source="fullName" />
          <TextField source="username" />
          <TextField source="email" />
          <TextField source="roles" />
          <ShowButton cellClassName={classes.button} />
          <EditButton cellClassName={classes.button} />
        </Datagrid>
      </List>
    );
  }
}

export default connect()(withStyles(styles)(User));

import React from "react";
import { withStyles } from "@material-ui/styles";
import {
  List,
  Datagrid,
  TextField,
  ReferenceField,
  ShowButton,
  EditButton,
} from "jazasoft";

import hasPrivilege from "../../utils/hasPrivilege";

const homeStyle = theme => ({
  button: {
    width: theme.spacing(2)
  }
});

export default withStyles(homeStyle)(({ classes, ...props }) => {
  const { roles, hasAccess } = props;
  return (
    <List {...props}>
      <Datagrid>
        <TextField label="Name" source="name" />
        <ReferenceField source="departmentId" reference="departments">
          <TextField source="name" />
        </ReferenceField>
        <ShowButton cellClassName={classes.button} />
        {hasPrivilege(roles, hasAccess, "activity", "update") && (
          <EditButton cellClassName={classes.button} />
        )}
      </Datagrid>
    </List>
  );
});

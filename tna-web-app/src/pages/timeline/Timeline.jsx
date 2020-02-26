import React from "react";
import { withStyles } from "@material-ui/styles";
import {
  List,
  Datagrid,
  TextField,
  EditButton,
  FunctionField,
  DeleteButton,
  CreateButton,
  ShowButton,
  Filter,
  ReferenceInput,
  SelectInput
} from "jazasoft";

import hasPrivilege from "../../utils/hasPrivilege";

const homeStyle = theme => ({
  button: {
    width: theme.spacing(2)
  }
});

const filters = (
  <Filter
    parse={({ buyerId }) => ({
      "buyer.id": buyerId
    })}
  >
    <ReferenceInput
      source="buyerId"
      reference="buyers"
      resource="buyers"
      sort={{ field: "name", order: "asc" }}
      xs={12}
      fullWidth={true}
      options={{ fullWidth: true }}
    >
      <SelectInput optionText="name" />
    </ReferenceInput>
  </Filter>
);

export default withStyles(homeStyle)(({ classes, ...props }) => {
  const { roles, hasAccess } = props;

  return (
    <List
      actions={({ basePath, roles, hasAccess }) => (
        <div>{hasPrivilege(roles, hasAccess, "timeline", "write") && <CreateButton basePath={basePath} showLabel={false} />}</div>
      )}
      {...props}
      filters={filters}
    >
      <Datagrid>
        <TextField label="Name" source="name" />
        <TextField label="Buyer" source="buyer.name" />
        <FunctionField label="Std Lead Time" render={record => (record.stdLeadTime ? `${record.stdLeadTime} days` : "")} />
        <FunctionField label="Approved" render={record => (record.approved ? "Yes" : "No")} />
        <ShowButton cellClassName={classes.button} />
        {hasPrivilege(roles, hasAccess, "timeline", "update", "default") && <EditButton cellClassName={classes.button} />}
        {hasPrivilege(roles, hasAccess, "timeline", "delete") && <DeleteButton cellClassName={classes.button} />}
      </Datagrid>
    </List>
  );
});

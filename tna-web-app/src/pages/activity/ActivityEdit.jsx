import React, { Component } from "react";
import { connect } from "react-redux";
import withStyles from "@material-ui/styles/withStyles";

import {
  Edit,
  MultiCardForm,
  FormCard,
  TextInput,
  ReferenceInput,
  SelectInput,
  BooleanInput,
  ArrayInput,
  SimpleFormIterator,
  required,
  minLength,
  maxLength,
  FormDataConsumer,
  SelectArrayInput
} from "jazasoft";
import Forbidden from "../../components/Forbidden";
import hasPrivilege from "../../utils/hasPrivilege";

const inputOptions = sm => ({
  xs: 12,
  sm,
  fullWidth: true,
  options: { fullWidth: true }
});

const styles = {};

class ActivityEdit extends Component {
  format = record => {
    if (record !== undefined) {
      let Activity = {
        ...record,
        notify: record && record.notify !== (null) ? record.notify.split(",").map(Number) : null,
        subActivityList:
          record && record.subActivityList && record.subActivityList.length
            ? record.subActivityList.sort((a, b) => a.id - b.id)
            : record.subActivityList
      };
      console.log({ Activity });

      return Activity;
    }
  };

  parse = record => {
    if (record && record.notify !== null) {
      const { notify, ...rest } = record;
      let filteredNotify = notify.filter(e => e !== record.departmentId);
      return { ...rest, notify: filteredNotify.join() }; // Parsing modified record to the API
    } else {
      return record;
    }
  };
  render() {
    const { roles, hasAccess, history, departments, classes, dispatch, activity, ...props } = this.props;
    if (!hasPrivilege(roles, hasAccess, "activity", "update")) {
      return <Forbidden history={history} />;
    }
    return (
      <Edit cardWrapper={false} record={{ subActivityList: [{}] }} parse={this.parse} format={this.format} {...props}>
        <MultiCardForm redirect="home">
          <FormCard title="Activity Details">
            <TextInput source="name" validate={[required(), minLength(2), maxLength(30)]} {...inputOptions(3)} />
            {
              <ReferenceInput source="departmentId" reference="departments" {...inputOptions(4)} validate={required()}>
                <SelectInput optionText="name" />
              </ReferenceInput>
            }

            <FormDataConsumer {...inputOptions(5)}>
              {({ formData }) => {
                if (formData.departmentId) {
                  let selectedDeptId = formData && formData.departmentId;
                  let totalDepartmentList = Object.keys(departments).map(e => departments[e]);
                  const rDepartments = formData.departmentId && totalDepartmentList.filter(e => selectedDeptId !== e.id);
                  const choices =
                    rDepartments &&
                    Object.values(rDepartments).map(({ id, name }) => ({
                      id,
                      name
                    }));

                  return <SelectArrayInput source="notify" label="Notify Departments" optionText="name" choices={choices} {...inputOptions(5)} />;
                } else {
                  return <SelectArrayInput source="notify" label="Notify Departments" optionText="name" choices={[]} {...inputOptions(5)} />;
                }
              }}
            </FormDataConsumer>
            <TextInput source="delayReasons" validate={[minLength(2)]} {...inputOptions(4)} />
            <div style={{ paddingTop: "1.5em", marginLeft: "3.5em", marginRight: "-2em" }}>
              <BooleanInput defaultValue={false} source="cLevel" label="C Level" {...inputOptions(2)} />
            </div>
            <BooleanInput
              defaultValue={false}
              source="isDefault"
              label="Default Activity"
              style={{ paddingTop: "1.5em", marginLeft: "-2em" }}
              {...inputOptions(2)}
            />
            <BooleanInput defaultValue={false} source="overridable" label="Overridable" style={{ paddingTop: "1.5em" }} {...inputOptions(2)} />
          </FormCard>
          <FormCard title="Sub Activities">
            <ArrayInput label="Sub Activity List" source="subActivityList" xs={12} fullWidth={true}>
              <SimpleFormIterator>
                <TextInput label="Name" source="name" validate={[required(), minLength(2)]} {...inputOptions(4)} />
                <TextInput label="Description" source="desc" {...inputOptions(8)} />
              </SimpleFormIterator>
            </ArrayInput>
          </FormCard>
        </MultiCardForm>
      </Edit>
    );
  }
}

const mapStateToProps = state => ({
  departments: state.jazasoft.resources["departments"] && state.jazasoft.resources["departments"].data
});

export default connect(mapStateToProps)(withStyles(styles)(ActivityEdit));

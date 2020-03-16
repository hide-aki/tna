import React, { Component } from "react";
import {
  FunctionField,
  Show,
  MultiCardShowLayout,
  ShowCard,
  TextField,
  Datagrid,
  ReferenceArrayField,
  SingleFieldList,
  ChipField,
  EditButton,
  BackButton,
  PageFooter
} from "jazasoft";
import hasPrivilege from "../../utils/hasPrivilege";

const Footer = ({ roles, hasAccess, resource, i18nKey, basePath, record }) => (
  <PageFooter>
    {hasPrivilege(roles, hasAccess, "activity", "update") && (
      <EditButton
        style={{ marginLeft: "1em" }}
        resource={resource}
        record={record}
        i18nKey={i18nKey}
        basePath={basePath}
        color="primary"
        variant="contained"
      />
    )}
    <BackButton style={{ marginLeft: "1em" }} variant="contained" />
  </PageFooter>
);

class ActivityView extends Component {
  format = record => {
    if (record && (record.notify !== null) && (record.notify !== "") ) {
      const { notify, ...rest } = record;
      return { ...rest, notify: notify.split(",") };
    } else {
      return record;
    }
  };

  render() {
    const { classes, dispatch, ...props } = this.props;
    return (
      <Show format={this.format} cardWrapper={false} {...props}>
        <MultiCardShowLayout footer={<Footer roles={props.roles} hasAccess={props.hasAccess} />}>
          <ShowCard title="Activity Details">
            <TextField source="name" />
            <FunctionField label="Department" render={record => (record.department ? record.department.name : "")} />
            <ReferenceArrayField label="Notify Departments" reference="departments" source="notify">
              <SingleFieldList>
                <ChipField allowEmpty={true} source="name" />
              </SingleFieldList>
            </ReferenceArrayField>

            <FunctionField label="C-Level" render={record => (record.cLevel ? "Yes" : "No")} />
            <FunctionField label="Default Activity" render={record => (record.isDefault ? "Yes" : "No")} />
            <FunctionField label="Overridable" render={record => (record.overridable ? "Yes" : "No")} />
            <TextField source="delayReasons" label="Delay Reason" />
          </ShowCard>
          <ShowCard
            title="Sub Activities"
            content={({ record = {} }) => {
              const data = record.subActivityList ? record.subActivityList.reduce((acc, el) => ({ ...acc, [el.id]: el }), {}) : {};
              const ids = record.subActivityList ? record.subActivityList.sort((a, b) => a.id - b.id).map(e => e.id) : [];
              return (
                <Datagrid data={data} ids={ids}>
                  <TextField source="name" />
                  <TextField source="desc" />
                </Datagrid>
              );
            }}
          />
        </MultiCardShowLayout>
      </Show>
    );
  }
}

export default ActivityView;

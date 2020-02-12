import React, { Component } from "react";
import {
  FunctionField,
  Show,
  MultiCardShowLayout,
  ShowCard,
  TextField,
  Datagrid,
  ReferenceField,
  ReferenceArrayField,
  SingleFieldList,
  ChipField,
  EditButton,
  BackButton,
  PageFooter
} from "jazasoft";
import hasPrivilege from "../../utils/hasPrivilege";

const Footer = ({ roles, hasAccess, resource, i18nKey, basePath }) => (
  <PageFooter>
    {hasPrivilege(roles, hasAccess, "activity", "update") && (
      <EditButton style={{ marginLeft: "1em" }} resource={resource} i18nKey={i18nKey} basePath={basePath} color="primary" variant="contained" />
    )}
    <BackButton style={{ marginLeft: "1em" }} variant="contained" />
  </PageFooter>
);

class ActivityView extends Component {
  format = record => {
    if (record && record.notify !== null) {
      const { notify, ...rest } = record;
      return { ...rest, notify: notify.split(",") };
    } else {
      return record;
    }
  };

  render() {
    const { roles, hasAccess, classes, ...props } = this.props;

    return (
      <Show format={this.format} cardWrapper={false} {...props}>
        <MultiCardShowLayout footer={<Footer />}>
          <ShowCard title="Activity Details">
            <TextField source="name" />
            <ReferenceField source="departmentId" reference="departments">
              <TextField source="name" />
            </ReferenceField>

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
            title="Subactivities"
            content={({ record = {} }) => {
              const data = record.subActivityList ? record.subActivityList.reduce((acc, el) => ({ ...acc, [el.id]: el }), {}) : {};
              const ids = record.subActivityList ? record.subActivityList.map(e => e.id) : [];
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

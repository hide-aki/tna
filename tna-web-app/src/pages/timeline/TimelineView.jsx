import React, { Component } from "react";

import { Show, TextField, ShowCard, MultiCardShowLayout, ReferenceField, PageFooter, Button, BackButton } from "jazasoft";

import EditIcon from "@material-ui/icons/Edit";
import MaterialTable from "material-table";
import { Icons } from "../../components/MaterialTableIcons";
import hasPrivilege from "../../utils/hasPrivilege";

const grassRoot = true;

const activityColumns = [
  { field: "name", title: "Name" },
  { field: "timefrom", title: "From" },
  { field: "leadTime", title: "Lead Time" }
];

const Footer = ({ roles, hasAccess, onEdit }) => (
  <PageFooter>
    {hasPrivilege(roles, hasAccess, "timeline", "update") && (
      <Button label="Edit" style={{ marginLeft: "1em" }} variant="contained" color="primary" onClick={onEdit}>
        <EditIcon />
      </Button>
    )}
    <BackButton style={{ marginLeft: "1em" }} variant="contained" />
  </PageFooter>
);

class TimelineView extends Component {
  onEdit = () => {
    this.props.history.push(`/timelines/${this.props.id}/edit`);
  };

  render() {
    const { roles, hasAccess } = this.props;

    return (
      <div>
        <Show cardWrapper={false} {...this.props}>
          <MultiCardShowLayout footer={<Footer roles={roles} hasAccess={hasAccess} onEdit={this.onEdit} />}>
            <ShowCard title="Timeline Details">
              <ReferenceField source="buyerId" reference="buyers">
                <TextField source="name" />
              </ReferenceField>
              <TextField source="name" />
              <TextField source="tnaType" label="TNA Type" />
            </ShowCard>
            <ShowCard
              title="Activities"
              content={({ record = {} }) => {
                if (!record.tActivityList) return null;
                let data = grassRoot
                  ? record.tActivityList
                      .sort((a, b) => a.activity.serialNo - b.activity.serialNo)
                      .flatMap(({ tSubActivityList, ...tActivity }) => {
                        return [
                          {
                            ...tActivity,
                            name: tActivity.activity && tActivity.activity.name,
                            timefrom:
                              tActivity.timeFrom && tActivity.timeFrom === "O"
                                ? "Order Date"
                                : tActivity.timeFrom === "E"
                                ? "Ex-Factory Date"
                                : tActivity.timeFrom
                                    .split(",")
                                    .map(e => {
                                      // Tranforming t_activity Id back to activityId
                                      for (let i = 0; i < record.tActivityList.length; i++) {
                                        if (Number(e) === record.tActivityList[i].id) {
                                          return record.tActivityList[i].name;
                                        }
                                      }
                                      return e;
                                    })
                                    .join(", "),
                            key: `P-${tActivity.id}`
                          },
                          ...tSubActivityList.map(e => ({
                            ...e,
                            name: e.subActivity && e.subActivity.name,
                            key: `C-${e.id}`,
                            parentKey: `P-${tActivity.id}`
                          }))
                        ];
                      })
                  : record.tActivityList;
                data = data.map(e => ({
                  ...e,
                  name: e.name
                }));
                return (
                  <MaterialTable
                    columns={activityColumns}
                    data={data}
                    icons={Icons}
                    options={{
                      toolbar: false,
                      search: false,
                      paging: false,
                      header: true
                    }}
                    style={{
                      boxShadow: "none",
                      width: "100%"
                    }}
                    parentChildData={
                      !grassRoot
                        ? null
                        : (row, rows) =>
                            rows.find(a => {
                              return a.key === row.parentKey;
                            })
                    }
                    onTreeExpandChange={this.onTreeExpandChange}
                  />
                );
              }}
            />
          </MultiCardShowLayout>
        </Show>
      </div>
    );
  }
}

export default TimelineView;

import React, { Component } from "react";

import { Show, TextField, ShowCard, MultiCardShowLayout, ReferenceField, PageFooter, Button, BackButton } from "jazasoft";

import EditIcon from "@material-ui/icons/Edit";
import MaterialTable from "material-table";
import { Icons } from "../../components/MaterialTableIcons";
import hasPrivilege from "../../utils/hasPrivilege";

const grassRoot = true;

const activityColumns = [
  { field: "name", title: "Name" },
  { field: "leadTime", title: "Lead Time" }
];

class TimelineView extends Component {
  onEdit = id => {
    this.props.history.push(`/timelines/${id}/edit`);
  };

  render() {
    const { roles, hasAccess, basePath, classes, tActivityList, ...props } = this.props;

    return (
      <div>
        <Show cardWrapper={false} {...props}>
          <MultiCardShowLayout footer={false}>
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
                      .flatMap(({ tSubActivityList, ...tActivity }, idx) => {
                        return [
                          {
                            ...tActivity,
                            name: tActivity.activity && tActivity.activity.name,
                            key: `P-${tActivity.id}`
                          },
                          ...tSubActivityList.map(e => ({
                            ...e,
                            name: e.subActivity && e.subActivity.name,
                            timeFrom: tActivity.timeFrom,
                            key: `C-${e.id}`,
                            parentKey: `P-${tActivity.id}`
                          }))
                        ];
                      })
                  : record.tActivityList;
                data = data.map(e => ({
                  ...e,
                  name: e.name,
                  leadTime: e.timeFrom === "O" ? `O + ` + e.leadTime : `E - ` + e.leadTime
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
            <PageFooter>
              {hasPrivilege(roles, hasAccess, "timeline", "update") && (
                <Button label="Edit" style={{ marginLeft: "1em" }} variant="contained" color="primary" onClick={_ => this.onEdit(props.id)}>
                  <EditIcon />
                </Button>
              )}
              <BackButton style={{ marginLeft: "1em" }} variant="contained" />
            </PageFooter>
          </MultiCardShowLayout>
        </Show>
      </div>
    );
  }
}

export default TimelineView;

import React, { Component } from "react";
import { connect } from "react-redux";

import withStyles from "@material-ui/styles/withStyles";
import Paper from "@material-ui/core/Paper";
import DownloadsIcon from "@material-ui/icons/GetApp";
import IconButton from "@material-ui/core/IconButton";
import Tooltip from "@material-ui/core/Tooltip";

import { PageHeader, RestMethods, FETCH_START, FETCH_END } from "jazasoft";

import Table from "jazasoft/lib/mui/components/Table";
import { dataProvider } from "../../App";
import handleError from "../../utils/handleError";

const styles = theme => ({
  root: {
    margin: theme.spacing(3)
  },
  headerCell: {
    padding: "12px",
    fontSize: 14,
    "&:last-child": {
      padding: "12px",
      fontSize: 14
    }
  },
  rowCell: {
    padding: "12px 16px",
    fontSize: 14,
    "&:last-child": {
      padding: "12px 16px",
      fontSize: 14
    }
  }
});

const ActionItem = ({ tooltip, icon, onClick }) => (
  <Tooltip title={tooltip} onClick={onClick}>
    <IconButton aria-label={tooltip}>{icon}</IconButton>
  </Tooltip>
);

class Downloads extends Component {
  state = {
    busy: false
  };

  onDownload = name => () => {
    this.setState({ busy: true });
    const options = {
      url: `templates`,
      method: "get",
      params: { name },
      responseType: "arraybuffer"
    };

    this.props.dispatch({ type: FETCH_START });
    dataProvider(RestMethods.CUSTOM, undefined, options)
      .then(response => {
        if (response.status === 200) {
          const header = response.headers["content-disposition"];
          var filename = header ? header.match(/filename="(.+)"/)[1] : `${name}.xlsx`;

          let blob = new Blob([response.data], {
            type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          });
          let link = document.createElement("a");
          link.href = window.URL.createObjectURL(blob);
          link.download = filename;
          link.click();
        }
        this.props.dispatch({ type: FETCH_END });
        this.setState({ busy: false });
      })
      .catch(err => {
        handleError(err, this.props.dispatch);
        this.props.dispatch({ type: FETCH_END });
        this.setState({ busy: false });
      });
  };

  render() {
    const { classes } = this.props;
    const columns = [{ title: "Name", dataKey: "name" }, { title: "", dataKey: "action", align: "right" }];

    const rows = [
      {
        name: "User Upload Template",
        action: (
          <ActionItem
            tooltip="Download"
            icon={<DownloadsIcon />}
            onClick={!this.state.busy ? this.onDownload("USER_UPLOAD_TEMPLATE") : null}
          />
        )
      }
    ];

    return (
      <div>
        <PageHeader title="Downloads" />

        <Paper className={classes.root}>
          <Table classes={{ headerCell: classes.headerCell, rowCell: classes.rowCell }} columns={columns} rows={rows} />
        </Paper>
      </div>
    );
  }
}

export default withStyles(styles)(connect()(Downloads));

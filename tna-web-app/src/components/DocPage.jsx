import React from "react";
import PropTypes from "prop-types";
import { withRouter } from "react-router-dom";

import { makeStyles } from "@material-ui/core/styles";
import Drawer from "@material-ui/core/Drawer";
import AppBar from "@material-ui/core/AppBar";
import Grid from "@material-ui/core/Grid";
import CssBaseline from "@material-ui/core/CssBaseline";
import Toolbar from "@material-ui/core/Toolbar";
import List from "@material-ui/core/List";
import Typography from "@material-ui/core/Typography";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import Hidden from "@material-ui/core/Hidden";

const drawerWidth = 300;

const useStyles = makeStyles(theme => ({
  root: {
    display: "flex"
  },
  container: {
    display: "flex",
    flexGrow: 1,
    flexDirection: "column"
  },
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
    marginLeft: `calc(100% - ${drawerWidth}px)`,
    width: drawerWidth
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0
  },
  drawerPaper: {
    width: drawerWidth
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing(3)
  },
  toolbar: theme.mixins.toolbar
}));

const DocPage = ({ navTitle, navLinks, headerElement, children, contentWidth, history }) => {
  const classes = useStyles();

  let drawerContent;
  if (navLinks.length > 0) {
    if (navLinks[0].navLinks) {
      drawerContent = navLinks.map(({ title, navLinks }, idx) => (
        <div key={idx}>
          <Typography style={{ marginLeft: "1em" }} variant="h6">
            {title}
          </Typography>
          <List dense={true} disablePadding>
            {navLinks.map(({ name, path, state }, idx) => (
              <ListItem key={idx} onClick={_ => history.push(path, state)}>
                <ListItemText primary={name} primaryTypographyProps={{ color: "secondary", variant: "subtitle1" }} />
              </ListItem>
            ))}
          </List>
        </div>
      ));
    } else {
      drawerContent = (
        <List component="nav" dense={true}>
          {navLinks.map(({ name, path, state }, idx) => (
            <ListItem key={idx} onClick={_ => history.push(path, state)}>
              <ListItemText primary={name} primaryTypographyProps={{ color: "secondary", variant: "subtitle1" }} />
            </ListItem>
          ))}
        </List>
      );
    }
  }

  return (
    <div className={classes.root}>
      <CssBaseline />
      <AppBar position="fixed" className={classes.appBar + " " + classes.appBarShift}>
        <Toolbar />
      </AppBar>

      <main style={{ display: "flex", flexGrow: 1, flexDirection: "column" }}>
        {headerElement}

        <div className={classes.content}>
          <Grid container justify="center">
            <Grid item md={contentWidth}>
              {children}
            </Grid>
          </Grid>
        </div>
      </main>
      <Hidden smDown>
        <Drawer
          className={classes.drawer}
          variant="permanent"
          anchor="right"
          classes={{
            paper: classes.drawerPaper
          }}
        >
          <div className={classes.toolbar} />
          <div style={{ padding: "1.5em" }}>
            <Typography variant="h5">{navTitle}</Typography>
          </div>

          {drawerContent}
        </Drawer>
      </Hidden>
    </div>
  );
};

const LinkType = PropTypes.shape({
  name: PropTypes.string,
  path: PropTypes.string
});

DocPage.propTypes = {
  headerElement: PropTypes.any,
  children: PropTypes.any,
  navTitle: PropTypes.string,
  navLinks: PropTypes.oneOfType([
    PropTypes.arrayOf(LinkType),
    PropTypes.arrayOf(
      PropTypes.shape({
        title: PropTypes.string,
        navLinks: PropTypes.arrayOf(LinkType)
      })
    )
  ]),
  contentWidth: PropTypes.number
};

DocPage.defaultProps = {
  navLinks: [],
  contentWidth: 8
};

export default withRouter(DocPage);

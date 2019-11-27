import React from 'react';
import PropTypes from 'prop-types';
import Button from '@material-ui/core/Button';
import { withStyles } from '@material-ui/styles';
import ForbiddenIcon from "mdi-material-ui/Cancel";
import classnames from 'classnames';

import {translate} from "jazasoft";

const styles = theme => ({
    container: {
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        [theme.breakpoints.up('md')]: {
            height: '100%',
        },
        [theme.breakpoints.down('sm')]: {
            height: '100vh',
            marginTop: '-3em',
        },
    },
    icon: {
        width: '9em',
        height: '9em',
    },
    message: {
        textAlign: 'center',
        fontFamily: 'Roboto, sans-serif',
        opacity: 0.5,
        margin: '0 1em',
    },
    toolbar: {
        textAlign: 'center',
        marginTop: '2em',
    },
});

const Forbidden = ({ classes, className, translate, title, history, match, location, staticContext, ...rest }) => (
    <div className={classnames(classes.container, className)} {...rest}>
        <div className={classes.message}>
            <ForbiddenIcon className={classes.icon} />
            <h1>{translate('forbidden.title')}</h1>
            <div>{translate('forbidden.message')}.</div>
        </div>
        <div className={classes.toolbar}>
            <Button variant="contained" onClick={() => history.go(-1)}>
                {translate('js.action.back')}
            </Button>
        </div>
    </div>
);

Forbidden.propTypes = {
    classes: PropTypes.object,
    className: PropTypes.string,
    title: PropTypes.string,
    translate: PropTypes.func.isRequired,
};

export default translate(withStyles(styles)(Forbidden));

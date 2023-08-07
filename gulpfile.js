const {series, src, dest} = require('gulp');
const concat = require('gulp-concat');
const clean = require('gulp-clean');
const terser = require('gulp-terser');
const cleanCSS = require('gulp-clean-css');
const del = require('del');

const MODULES = 'node_modules/';
const DEPENDENCIES = require('./vendor.json');;


async function cleanAll(cb) {
    await del(DEPENDENCIES.vendor);
    cb();
}


function styles(cb) {
    let styles = [];
    DEPENDENCIES.styles.forEach(x => styles.push(MODULES + x));

    src(styles)
            .pipe(concat('vendor.min.css'))
            .pipe(cleanCSS({compatibility: 'ie8'}))
            .pipe(dest(DEPENDENCIES.vendor));
    cb();
}


function javascripts(cb) {
    let jsFiles = [];
    DEPENDENCIES.javascripts.forEach(x => jsFiles.push(MODULES + x));

    src(jsFiles)
            .pipe(terser())
            .pipe(concat('vendor.min.js'))
            .pipe(dest(DEPENDENCIES.vendor));
    cb();
}


function directories(cb) {
    let dirList = [];
    DEPENDENCIES.directories.forEach(x => dirList.push(MODULES + x));

    if (dirList.length > 0) {
        src(dirList).pipe(dest(DEPENDENCIES.vendor));
    }
    cb();
}


function drops(cb) {
    DEPENDENCIES.drops.forEach(x => {
        del(MODULES + x);
    });

    cb();
}


exports.default = series(cleanAll, drops, directories, javascripts, styles);
No newline at end of file

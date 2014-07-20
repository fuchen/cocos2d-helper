var path = require('path');
var fs = require('fs');
var sh = require("shelljs");

var DEBUG = false, CAT = false, PROJ_DIR;

function init() {
    var opt = process.argv[2];
    if (opt) {
        opt = opt.toUpperCase();
    }

    if (opt === 'DEBUG') {
        DEBUG = true;
    } else if (opt === 'CAT') {
        CAT = true;
    }

    PROJ_DIR = process.env['PROJ_DIR'];
}

function deploy() {
    var srcDir = path.resolve(PROJ_DIR, 'src');
    var dstDir = path.resolve(PROJ_DIR, 'frameworks/runtime-src/proj.android/assets/src');

    // clear dest dir
    sh.rm('-R', dstDir);
    sh.mkdir('-p', dstDir);


    if (DEBUG) {
        copySrc(srcDir, dstDir);
    } else {
        compileTo(srcDir, dstDir);
    }
}

function compileTo(srcDir, dstDir) {
    var files = sh.ls('-R', srcDir);
    files.forEach(function (f) {
        if (!f.match(/\.lua$/)) {
            return;
        }

        var dir = path.dirname(path.resolve(dstDir, f));
        if (!fs.existsSync(dir)) {
            sh.mkdir('-p', dir)
        }

        sh.exec("luajit -b " + path.resolve(srcDir, f) + ' ' + path.resolve(dstDir, f));
    })
}

function copySrc(srcDir, dstDir) {
    sh.cp('-Rf', srcDir +'/*', dstDir);
    return;
}

init();
deploy();

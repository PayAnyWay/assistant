const path = require('path');
const webpack = require('webpack');

const webpackMerge = require('webpack-merge');
const kotlinConfig = require('./webpack.kotlin.js');

const KotlinWebpackPlugin = require('@jetbrains/kotlin-webpack-plugin');
// const {CleanWebpackPlugin} = require('clean-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = function (env) {
    return webpackMerge.merge(kotlinConfig(env), {
        mode: 'production',

        plugins: [
            // new CleanWebpackPlugin(),
            new KotlinWebpackPlugin({
                src: path.join(__dirname, 'src/main'),
                optimize: true,
                verbose: true,
                librariesAutoLookup: true
            })
        ],

        optimization: {
            minimizer: [new TerserPlugin({
                parallel: true
            })]
        }
    });
};

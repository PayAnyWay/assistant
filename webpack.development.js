const path = require('path');

const merge = require('webpack-merge');
const kotlinConfig = require('./webpack.kotlin.js');

const KotlinWebpackPlugin = require('@jetbrains/kotlin-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = merge(kotlinConfig, {
    mode: 'development',

    plugins: [
        new KotlinWebpackPlugin({
            src: path.join(__dirname, 'src/main'),
            optimize: false,
            verbose: true,
            librariesAutoLookup: true
        }),
        new HtmlWebpackPlugin({
            template: './asset/index.html',
            inject: 'head'
        })
    ],

    devServer: {
        port: 3333,
        disableHostCheck: true,

        historyApiFallback: true,
        hot: true,
    }
});


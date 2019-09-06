const path = require('path');

const merge = require('webpack-merge');
const kotlinConfig = require('./webpack.kotlin.js');

const KotlinWebpackPlugin = require('@jetbrains/kotlin-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const {CleanWebpackPlugin} = require('clean-webpack-plugin');

module.exports = merge(kotlinConfig, {
    mode: 'development',

    output: {
        filename: 'js/assistant-builder.js',
        library: "Assistant",
        libraryTarget: "var"
    },

    plugins: [
        new CleanWebpackPlugin({
            // be very careful with this plugin, test new paths with:
            // dry: true,
            cleanOnceBeforeBuildPatterns: [
                'dist', 'kotlin_build'
            ]
        }),
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


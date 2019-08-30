const path = require('path');

const merge = require('webpack-merge');
const kotlinConfig = require('./webpack.kotlin.js');

const KotlinWebpackPlugin = require('@jetbrains/kotlin-webpack-plugin');
const {CleanWebpackPlugin} = require('clean-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = merge(kotlinConfig, {
    mode: 'production',

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

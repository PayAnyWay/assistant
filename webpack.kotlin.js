const path = require('path');
const config = require('./config.json');

module.exports = {
    entry: 'kotlinApp', // kotlinApp is the default module name

    output: {
        filename: 'js/assistant-builder.js',
        library: "Assistant",
        libraryTarget: "var"
    },

    resolve: {
        // "kotlin_build" is the build output directory
        modules: ['kotlin_build', 'node_modules']
    },

    // [OPTIONAL] To enable sourcemaps, source-map-loader should be configured
    module: {
        rules: [
            {
                test: /\.js$/,
                include: path.resolve(__dirname, 'kotlin_build'),
                exclude: [
                    /kotlin\.js$/, // Kotlin runtime doesn't have sourcemaps at the moment
                ],
                use: ['source-map-loader'],
                enforce: 'pre'
            },
            {
                test: /\.js$/,
                loader: "string-replace-loader",
                options: {
                    multiple: [
                        {
                            search: "{{PRODUCTION_HOST}}",
                            replace: config.PRODUCTION_HOST,
                            flags: "ig"
                        },
                        {
                            search: "{{DEMO_HOST}}",
                            replace: config.DEMO_HOST,
                            flags: "ig"
                        }
                    ]
                }
            }
        ]
    }
};

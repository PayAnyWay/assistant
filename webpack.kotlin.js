const path = require('path');
const config = require('./config');

function resolveHostByEnv(env) {
    if (env) {
        if (env.demo) return config.DEMO_HOST
        if (env.development) return config.DEVELOPMENT_HOST
    }

    return config.PRODUCTION_HOST
}

module.exports = function (env) {
    return {
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
                        search: "{{HOST}}",
                        replace: resolveHostByEnv(env),
                        flags: "ig"
                    }
                }
            ]
        }
    }
};

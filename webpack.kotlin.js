const path = require('path');

module.exports = {
    entry: 'kotlinApp', // kotlinApp is the default module name

    output: {
        path: path.join(__dirname, 'dist')
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
                use: ['source-map-loader'],
                enforce: 'pre'
            }
        ]
    }
};

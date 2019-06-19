window.onload = function() {

    const jsonRpcRequestRegExp = /(.*)\/json-rpc\/(.*)/
    let requestId = 1;

    const jsonRpcRequestInterceptor = function(request) {
        const match = jsonRpcRequestRegExp.exec(request.url);
        if (match !== null) {
            request.url = match[1];
            request.body = JSON.stringify({
                id: requestId++,
                method: match[2],
                params: JSON.parse(request.body)
            }, null, 2);
        }

        return request;
    }

    const ui = SwaggerUIBundle({
        url: "v2/api-docs",
        dom_id: '#swagger-ui',
        layout: "StandaloneLayout",
        deepLinking: true,
        displayRequestDuration: true,
        docExpansion: "none",
        plugins: [
            SwaggerUIBundle.plugins.DownloadUrl
        ],
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],
        requestInterceptor: jsonRpcRequestInterceptor
    });

    window.ui = ui;
}

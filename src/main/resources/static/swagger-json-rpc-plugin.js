window.onload = function() {

    const jsonRpcRequestRegExp = /(.*)\/json-rpc\/(.*)/;
    let requestId = 1;

    function jsonRpcRequestInterceptor(request) {
        const match = jsonRpcRequestRegExp.exec(request.url);
        if (match !== null) {
            request.url = match[1];
            request.body = JSON.stringify({
                id: requestId++,
                method: match[2],
                params: request.body ? JSON.parse(request.body) : null,
                jsonrpc: '2.0'
            }, null, 2);
            request.headers['Content-Type'] = 'application/json';
        }

        return request;
    }

    function createBeautifulJsonRpcMarker() {
        return {
            wrapComponents: {
                DeepLink: (Original, { React }) => (props) => {
                    if (props['text'] && props['text'].startsWith('[JSON-RPC] ')) {
                        const jsonRpcProps = Object.assign({}, props);
                        jsonRpcProps['text'] = props['text'].slice(11);

                        return React.createElement('div', null,
                            React.createElement('span', {className: 'json-rpc'}, 'JSON-RPC'),
                            React.createElement(Original, jsonRpcProps)
                        );
                    }

                    return React.createElement(Original, props);
                }
            }
        }
    }

    const ui = SwaggerUIBundle({
        url: "v2/api-docs",
        dom_id: '#swagger-ui',
        layout: "StandaloneLayout",
        deepLinking: true,
        displayRequestDuration: true,
        docExpansion: "none",
        plugins: [
            SwaggerUIBundle.plugins.DownloadUrl,
            createBeautifulJsonRpcMarker
        ],
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],
        requestInterceptor: jsonRpcRequestInterceptor
    });

    window.ui = ui;
};

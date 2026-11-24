#!/bin/bash

echo "========================================="
echo "  WebSocket Test Client"
echo "========================================="
echo ""
echo "Starting server on http://localhost:8080"
echo "Open the URL above in your browser."
echo "Press Ctrl+C to stop."
echo ""

npx serve . -p 8080 -s --no-clipboard

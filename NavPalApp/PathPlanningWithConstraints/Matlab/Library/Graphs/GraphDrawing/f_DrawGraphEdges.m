function f_DrawGraphEdges(graphVertices, graphEdges, Color, LineWidth)
%
% Draw the edges of a graph to the current figure
%
    numberOfEdges = size(graphEdges, 1);
    for i=1:numberOfEdges

        if (graphEdges(i, 4) == 0)
            continue;
        end

        % Get the source and destination vertices of the current edge
        edgeSourceVertex      = graphEdges(i, 1);
        edgeDestinationVertex = graphEdges(i, 2);

        % Get the x-y coordinates for the current edge vertex
        edgeX1 = graphVertices(edgeSourceVertex, 2);
        edgeY1 = graphVertices(edgeSourceVertex, 3);
        edgeX2 = graphVertices(edgeDestinationVertex, 2);
        edgeY2 = graphVertices(edgeDestinationVertex, 3);

        hold on

        line([edgeX1, edgeX2], [edgeY1, edgeY2], 'Color', Color, 'LineWidth', LineWidth);

        hold off

    end

end

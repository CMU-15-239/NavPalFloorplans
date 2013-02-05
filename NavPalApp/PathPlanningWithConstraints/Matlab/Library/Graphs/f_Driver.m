function f_Driver()

    % Create a list of vertices
    vertices = [1, 10, 20, 0;
                2,  2,  5, 0;
                3, 45, 48, 0;
                4, 23, 35, 0;
                5, 47, 25, 0;
                6, 12, 15, 0;
                7, 34,  5, 0;
                8,  6, 25, 0;
                9, 39, 12, 0;
                10, 42, 11, 0];

    edges = f_CreateFullyConnectedGraph(vertices)


end


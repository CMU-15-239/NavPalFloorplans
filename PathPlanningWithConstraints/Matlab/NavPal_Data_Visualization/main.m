function main()

    % Original "Rooms" file produced by python script
    nsh_F1_Rooms_File_Generated_from_Python_Using_Original_SVG_File_from_SVN = 'C:\Users\rCommerce\Documents\Temp\New folder\PathPlanningWithConstraints_394\PythonScripts\ParsingSVGFiles\nsh_1_f_vec_room_python_output.txt';

    % Rooms files generated from the 2011 SVG files, which were derived
    % from the colored PDF floor plan files from 2011.
    nsh_2011_F1_v48_1_rooms_python_output                  = 'C:\Users\rCommerce\Documents\Temp\New folder\PathPlanningWithConstraints_394\PythonScripts\ParsingSVGFiles\2011_Newell-Simon_GIS-1_v48_1_room_python_output.txt';
    nsh_2011_F1_v48_1_rooms_python_output_patterns_removed = 'C:\Users\rCommerce\Documents\Temp\New folder\PathPlanningWithConstraints_394\PythonScripts\ParsingSVGFiles\2011_Newell-Simon_GIS-1_v48_1_hash_pattern_removed_room_python_output.txt';
    
    % Other Files
    
    nsh_2011_F1_v48_1_rooms_python_output                  = 'C:\Users\rCommerce\Documents\Temp\New folder\PathPlanningWithConstraints_394\PythonScripts\ParsingSVGFiles\2011_Newell-Simon_GIS-1_v48_1_room_python_output.txt';
    nsh_2011_F1_v48_1_rooms_python_output_patterns_removed = 'C:\Users\rCommerce\Documents\Temp\New folder\PathPlanningWithConstraints_394\PythonScripts\ParsingSVGFiles\2011_Newell-Simon_GIS-1_v48_1_hash_pattern_removed_room_python_output.txt';

    nsh_2012_F1_v48_1_rooms_python_output                         = 'C:\Users\rCommerce\Documents\Temp\New folder\PathPlanningWithConstraints_394\PythonScripts\ParsingSVGFiles\2012_Newell-Simon_GIS-1_v48_1_room_python_output.txt';
    nsh_2012_F1_v48_1_rooms_python_output_output_patterns_removed = 'C:\Users\rCommerce\Documents\Temp\New folder\PathPlanningWithConstraints_394\PythonScripts\ParsingSVGFiles\2012_Newell-Simon_GIS-1_v48_1_hash_patterns_removed_room_python_output.txt';

    floor = nsh_F1_Rooms_File_Generated_from_Python_Using_Original_SVG_File_from_SVN;
    f_VisualizeRooms(1, floor);
    
    floor = nsh_2011_F1_v48_1_rooms_python_output_patterns_removed;
    f_VisualizeRooms(2, floor);

end

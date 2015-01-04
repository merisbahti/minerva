function [x,y] = scan_data(input_file)
  % Attempts to scan a file and read x- and y-coordinates
  % from a file.
  % Input: input_file = path to the input file.
  % Output: x = x-coordinates read from the input file.
  %         y = y-coordinates read from the input file.
	fileID = fopen(input_file);
	data = textscan(fileID,'%u %f', 'delimiter', '\t');
	x = data{1,1};
	y = data{1,2};

function [relRank, scores] = scan_rank_noun_test(input_file)
  % Attempts to scan a file and read x- and y-coordinates
  % from a file.
  % Input: input_file = path to the input file.
  % Output: x = x-coordinates read from the input file.
  %         y = y-coordinates read from the input file.
	fileID = fopen(input_file);
	data = textscan(fileID,'%u\t%f\t%u');
	x = data{1,1};
	y = data{1,2};
	z = data{1,3};
  for idx = 1:numel(x)
    xfin(end+1) = cast(x(idx), 'double');
  end
  for idx = 1:numel(z)
    zfin(end+1) = cast(z(idx), 'double');
  end
  relRank = 1 - (xfin ./ zfin);
  scores = y;

function [x,y,z] = scan_punch_ranked_top(input_file)
  graphics_toolkit ("gnuplot")
  % Attempts to scan a file and read x- and y-coordinates
  % from a file.
  % Input: input_file = path to the input file.
  % Output: x = top answers.
  %         y = re-ranked top answers.
  %         z = punched re-ranked top answers..
	fileID = fopen(input_file);
	data = textscan(fileID,'%d %d %d','delimiter', '\t');
	x = data{1,1};
	y = data{1,2};
	z = data{1,3};
  xmean = mean(x)
  ymean = mean(y)
  zmean = mean(z)
  xmedian = median(x)
  ymedian = median(y)
  zmedian = median(z)
  xmrr = mrr(x)
  ymrr = mrr(y)
  zmrr = mrr(z)

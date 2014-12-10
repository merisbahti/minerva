function [median, mrr] = scan_median_and_mrr(infile)
	fileID = fopen(infile)
	data = textscan(fileID,'%u\t%u');
	x = data{1,1};
	y = data{1,2};
  for idx = 1:numel(x)
    xfin(end+1) = cast(x(idx), 'double');
  end
  for idx = 1:numel(y)
    yfin(end+1) = cast(y(idx), 'double');
  end
  mrr = 0.0;
  for idx = 1:numel(x)
      element = x(idx);
      ec = cast(element, 'double');
      mrr = mrr + 1/ec;
  end
  mrr = mrr/size(x)(1);
  median = (median(xfin./yfin));

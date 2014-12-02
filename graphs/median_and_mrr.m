function [median, mrr] = median_and_mrr(infile)
	fileID = fopen(infile)
	data = textscan(fileID,'%u\t%u');
	x = data{1,1};
	y = data{1,2};
  mrr = 0.0;
  for idx = 1:numel(x)
      element = x(idx);
      ec = cast(element, 'double');
      mrr = mrr + 1/ec;
  end
  disp(size(x))
  mrr = mrr/size(x)(1);
  median = (median(x));

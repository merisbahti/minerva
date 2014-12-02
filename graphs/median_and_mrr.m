function [median, mrr] = median_and_mrr()
  [x,y] = scan_data("rankNounsMedianMRR1.txt");
  mrr = 0.0;
  for idx = 1:numel(x)
      element = x(idx);
      ec = cast(element, 'double');
      mrr = mrr + 1/ec;
  end
  disp(size(x))
  mrr = mrr/size(x)(1);
  median = (median(x));

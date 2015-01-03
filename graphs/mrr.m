function mrr_ans = mrr(x) 
  mrr_ans = 0.0;
  for idx = 1:numel(x)
      element = x(idx);
      ec = cast(element, 'double');
      mrr_ans = mrr_ans + 1/ec;
  end
  mrr_ans = mrr_ans/numel(x);

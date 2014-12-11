function median_plotter()
  graphics_toolkit ("gnuplot")
  % Scan the data from the incomplete data-set.
  [median1p,   mrr1p  ] = scan_median_and_mrr("rankNounsMedianMrrP1.txt");
  [median10p,  mrr10p ] = scan_median_and_mrr("rankNounsMedianMrrP10.txt");
  [median100p, mrr100p] = scan_median_and_mrr("rankNounsMedianMrrP100.txt");
  [median200p, mrr200p] = scan_median_and_mrr("rankNounsMedianMrrP200.txt");
  [median1d,   mrr1d  ] = scan_median_and_mrr("rankNounsMedianMrrD1.txt");
  [median10d,  mrr10d ] = scan_median_and_mrr("rankNounsMedianMrrD10.txt");
  [median100d, mrr100d] = scan_median_and_mrr("rankNounsMedianMrrD100.txt");
  [median200d, mrr200d] = scan_median_and_mrr("rankNounsMedianMrrD200.txt");
  mediansp = [median1p, median10p, median100p, median200p];
  mediansd = [median1d, median10d, median100d, median200d];
  mrrp  = [mrr1p, mrr10p, mrr100p, mrr200p];
  mrrd  = [mrr1d, mrr10d, mrr100d, mrr200d];
  xVals   = [1      , 10      , 100      , 200];

  plot(xVals,mediansp,'marker','o','color', [46/255 204/255 113/255]);
  hold on;
  plot(xVals,mrrp,'marker','o','color', [230/255 126/255 34/255]);
  plot(xVals,mediansd,'marker','o','color', [155/255 89/255 182/255]);
  plot(xVals,mrrd,'marker','o','color', [52/255 152/255 219/255]);
  xlim([1 200])
  legend('Normalized Median Paragraphs', 'Mean Reciprocal Rank Paragraphs', 'Normalized Median Documents', 'Mean Reciprocal Rank Documents', 'location', 'northeast');
  xlabel('#Passages')
  ylabel('Score/Value')
  %a=[cellstr(num2str(get(gca,'ytick')'*100))]; 
  %pct = char(ones(size(a,1),1)*'%'); 
  %new_yticks = [char(a),pct];
  %set(gca,'yticklabel',new_yticks) 
  set(gcf, 'PaperPosition', [0 0 8 5]); %Position plot at left hand corner with width 5 and height 5.
  set(gcf, 'PaperSize', [8 5]); %Set the paper to have width 5 and height 5.
  saveas(gcf, 'median', 'pdf') %Save figure
  %print('-dpng','-r96','test')
  axis equal;
  hold off;


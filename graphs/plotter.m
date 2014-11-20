function plotter()
  graphics_toolkit ("gnuplot")
  % Scan the data from the incomplete data-set.
  [xbm25,ybm25] = scan_data("bm25.txt");
  [xtfdf,ytfdf] = scan_data("tfdf.txt");
  [xbm25doc,ybm25doc] = scan_data("bm25-doc.txt");
  [xtfdfdoc,ytfdfdoc] = scan_data("tfdf-doc.txt");
  %rgb(230, 126, 34)
  plot(xbm25,ybm25,'marker','o','color', [230/255 126/255 34/255]);
  hold on;
  %rgb(52, 152, 219)
  plot(xtfdf,ytfdf,'marker','o','color', [52/255 152/255 219/255]);
  plot(xbm25doc,ybm25doc,'marker','o','color', [211/255 84/255 0/255]);
  plot(xtfdfdoc,ytfdfdoc,'marker','o','color', [41/255 128/255 185/255]);
  xlim([1 350])

  legend('BM25 Paragraphs', 'TFDF Paragraphs', 'BM25 Documents', 'TFDF Documents');
  xlabel('#Passages')
  ylabel('Answers present')
  a=[cellstr(num2str(get(gca,'ytick')'*100))]; 
  pct = char(ones(size(a,1),1)*'%'); 
  new_yticks = [char(a),pct];
  set(gca,'yticklabel',new_yticks) 
  set(gcf, 'PaperPosition', [0 0 8 5]); %Position plot at left hand corner with width 5 and height 5.
  set(gcf, 'PaperSize', [8 5]); %Set the paper to have width 5 and height 5.
  saveas(gcf, 'test', 'pdf') %Save figure
  %print('-dpng','-r96','test')
  axis equal;
  hold off;

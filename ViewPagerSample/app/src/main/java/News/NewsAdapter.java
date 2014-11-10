package News;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import com.samples.epamtraining.viewpagersample.R;
import com.samples.epamtraining.viewpagersample.R.*;

public class NewsAdapter extends BaseAdapter {
    private List<NewsItem> newsList;
    private Context mContext;

    public NewsAdapter(List<NewsItem> newsList, Context context) {
        this.newsList = newsList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cnView = convertView;
        NewsHolder newsHolder;
        if (cnView == null) {
            cnView = LayoutInflater.from(mContext).inflate(layout.newsitem, null);
            newsHolder = new NewsHolder();
            newsHolder.tvAuthor = (TextView) cnView.findViewById(R.id.tvAuthor);
            newsHolder.tvDate = (TextView) cnView.findViewById(R.id.tvDate);
            newsHolder.tvText = (TextView) cnView.findViewById(R.id.tvText);
            newsHolder.tvTitle = (TextView) cnView.findViewById(id.tvTitle);
            cnView.setTag(newsHolder);
        } else {
            newsHolder = (NewsHolder) cnView.getTag();
        }
        NewsItem item = newsList.get(position);
        newsHolder.tvTitle.setText(item.getTitle());
        newsHolder.tvDate.setText(item.getDate());
        newsHolder.tvText.setText(item.getText());
        newsHolder.tvAuthor.setText(item.getAuthor());
        return cnView;
    }

    private class NewsHolder {
        private TextView tvAuthor;
        private TextView tvDate;
        private TextView tvTitle;
        private TextView tvText;
    }
}

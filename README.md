bulk-web-article-downloader
===========================

tweets + shorten URLs (or URL redirection) + article extraction from web-page (boilerpipe) + MapReduce (MRv2)

From given list of tweets and corresponding refering URL, bulk-web-article-downloader aggregates tweets for a URL. For all such URL, it handles the URL redirection, extract article-text from the URL, and produce JSON file with the extracted information.

Following is the JSON format:
{
    "downloaded_time": 1411031928918,
    "tweets": [
        "8769",
        "3432",
        "3434",
        "34343",
        "35324343",
        "35324343",
        "35324343",
        "35324343"
    ],
    "title": "New Study Sees Atlantic Warming Behind a Host of Recent Climate Shifts - NYTimes.com",
    "short_url": "http://t.co/TrKhXr7YeW",
    "expanded_url": "http://dotearth.blogs.nytimes.com/2014/08/03/new-study-sees-atlantic-warming-behind-a-host-of-recent-climate-shifts/?_php=true&_type=blogs&_r=0",
    "content": "August 3, 2014 1:05 pmAugust 3, 2014 1:05 pmUpdated, 3:10 p.m. | Using climate models and observations, a fascinating study in this week’s issue of Nature Climate Change points to a marked recent warming of the Atlantic Ocean as a powerful shaper of a host of notable changes in climate and ocean patterns in the last couple of decades — including .....bla bla...."
}

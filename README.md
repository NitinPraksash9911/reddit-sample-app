# reddit-sample-app
This app loads a random post and it's comments using reddit random post api

1. Comment tree is implemented using third party library (com.muditsen.multilevelrecyclerview:multilevelview)
   Link for this library : https://github.com/muditsen/multilevelrecyclerview.
   
2. There is FAB button on Home Screen just reload the new post by clicking it and it will also all the comments but comment thread will      shown upto third level.

3. Comments are comming along with the rondom post response so we are not calling another api for comments and threaded comments are also   coming with (threaded means replies of comments) but reposne of threaded comments response are almost same with post data response so we   have created another POJO class for generalizing all the comments data.

4. And login api is also integrated in this app but it is just for show case that how you can login in reddit 

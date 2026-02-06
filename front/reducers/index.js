// reducers/index.js

import { combineReducers }  from  'redux';
import authReducer          from  './authReducer';
import postReducer          from  './postReducer';
import commentReducer       from  './commentReducer';
import followReducer        from  './followReducer';
import likeReducer          from  './likeReducer';
import retweetReducer       from  './retweetReducer';

const rootReducer = combineReducers({
    auth: authReducer , 
    post: postReducer , 
    comment: commentReducer , 
    follow: followReducer , 
    like: likeReducer , 
    retweet: retweetReducer , 
});

export default rootReducer;



// front/reducers/
//  ┣ authReducer.js
//  ┣ postReducer.js
//  ┣ commentReducer.js
//  ┣ followReducer.js
//  ┣ likeReducer.js
//  ┣ retweetReducer.js
//  ┗ index.js   → combineReducers
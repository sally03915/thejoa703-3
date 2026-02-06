import { useState, useEffect } from "react";  // react view 에서 상태확인
import { useDispatch, useSelector } from "react-redux"; // 치킨집 (액션-> , reducer)
import { Spin, Alert, message, Tabs } from "antd";  //부품
import InfiniteScroll from "react-infinite-scroll-component";   // 무한스크롤

import {
  fetchPostsPagedRequest,
  fetchLikedPostsRequest,
  fetchMyAndRetweetsRequest,
  updatePostRequest,
} from "../reducers/postReducer";  // 글쓰기 액션
import {
  addLikeRequest,
  removeLikeRequest,
  fetchMyLikesRequest,
} from "../reducers/likeReducer";  // 좋아요 액션
import {
  toggleFollowRequest,   
  loadFollowingsRequest,
} from "../reducers/followReducer";
import { fetchMyRetweetsRequest } from "../reducers/retweetReducer"; // 리트윗액션

import PostList from "../components/PostList";  // 글쓰기 리스트
import EditPostModal from "../components/EditPostModal";  //수정

export default function Home(){
    // code
    const dispatch = useDispatch();  // 액션발생시 치킨집에 알려줌 
    //// reducer 상태
    const {user} =  useSelector( (state) => state.auth);
    const {posts,  likedPosts,   myAndRetweets,  
           loading,  error,  hasNext  // redux에서 다음 페이지데이터 더 있는지 여부 ##
    } =  useSelector( (state) => state.post);
    const { likes= {},       likesCount ={},    loading: likeLoading } =  useSelector( (state) => state.like);
    const { followingsMap ,    loading: followLoading  } =  useSelector( (state) => state.follow);//loading 이름충돌
    const {retweets , retweetsCount} =  useSelector( (state) => state.retweet);

    //// 컴포넌트 내부에서만 사용되는 UI 제어용 
    const [expandedPostId, setExpandedPostId] = useState(null); //특정글 클릭시 상세내용보기
    const [isEditModalVisible, setIsEditModalVisible] = useState(false);// 글수정 모달
    const [editPost, setEditPost] = useState(null); //모달열때, 선택할 글의 데이터를 담아두고 , 수정완료시 서버전달
    const [uploadFiles, setUploadFiles] = useState([]); // 글수정시 이미지 업로드
    const [pageAll, setPageAll] = useState(1);  // 무한스크롤 불러올때 "현재 페이지 번호" 관리

    //// 이벤트
    // 첫로딩
    useEffect( ()=> {
        dispatch(fetchPostsPagedRequest({ page: 1, size: 10 }));
        setPageAll(2);

        if (user) {
            dispatch(fetchLikedPostsRequest({ page: 1, size: 10 }));
            dispatch(fetchMyAndRetweetsRequest({ page: 1, size: 10 }));
            dispatch(fetchMyLikesRequest({ userId: user.id }));
            dispatch(fetchMyRetweetsRequest({ userId: user.id }));
            dispatch(loadFollowingsRequest());
        }
    } , [dispatch , user]);

    // 무한 스크롤 추가 로딩
    const fetchMoreAll = () => {
        if (!hasNext) return;
        dispatch(fetchPostsPagedRequest({ page: pageAll, size: 10 }));
        setPageAll((prev) => prev + 1);  //다음페이지 셋팅
    };

    // 글 수정 모달
    const handleEdit = (post) => {
        setEditPost(post);  // 수정글 넣기
        setIsEditModalVisible(true); // 화면에 보이기
        setUploadFiles([]);  //업로드파일 빈파일
    };

    // 글 수정 제출
    const handleEditSubmit = (values) => {
        dispatch(updatePostRequest({
            postId: editPost.id,
            dto: {
                content: values.content,
                hashtags: Array.isArray(values.hashtags)
                ? values.hashtags.join(",")
                : values.hashtags,
            },
            files: uploadFiles,
        }));
        setIsEditModalVisible(false);
        setEditPost(null);
    };

    // 좋아요 토글
  const handleToggleLike = (postId) => {
    if (!user) {
      message.warning("로그인 후 이용 가능합니다.");
      window.location.href = "/login";
      return;
    }
    const key = String(postId);
    if (likes[key] === true) {
      dispatch(removeLikeRequest({ postId }));
    } else {
      dispatch(addLikeRequest({ postId }));
    }
    dispatch(fetchLikedPostsRequest({ page: 1, size: 10 }));
  };

    // 팔로우 토글
  const handleToggleFollow = (authorId) => {
    if (!user) {
      message.warning("로그인 후 이용 가능합니다.");
      window.location.href = "/login";
      return;
    }
    if (!authorId) {
      message.error("팔로우 대상 ID가 없습니다.");
      return;
    }
    dispatch(toggleFollowRequest(authorId));
  };    

    if(  loading  &&  posts.length === 0 ) return <Spin tip="글 목록을 불러오는 중..." />;
    if(  error  )  return <Alert  type="error"  message="글목록 불러오기 실패"  description={error} />;

    /////////////////////////////////
    // view
    return <>
        <Tabs   defaultActiveKey="all"  centered  onChange={(key)=>{
            if(key === "liked"  && user){
                   dispatch(fetchLikedPostsRequest({ page:1, size:10})); 
            }
            if(key === "my"  && user){
                   dispatch(fetchMyAndRetweetsRequest({ page:1, size:10})); 
            } 
        }}>
            <Tabs.TabPane  tab="전체 글"  key="all">
                <InfiniteScroll
                    dataLength={posts.length}
                    next={fetchMoreAll}
                    hasMore={hasNext}
                    loader={<Spin tip="더 불러오는 중..." />}
                    endMessage={<p style={{ textAlign: "center" }}>모든 글을 불러왔습니다</p>}
                    style={{ overflow: "visible" }}
                >
                  <PostList
                    posts={posts}
                    user={user}
                    likes={likes}
                    likesCount={likesCount}
                    retweetedPosts={retweets}
                    retweetsCount={retweetsCount}
                    expandedPostId={expandedPostId}
                    setExpandedPostId={setExpandedPostId}
                    handleToggleLike={handleToggleLike}
                    handleToggleFollow={handleToggleFollow}
                    handleEdit={handleEdit}
                    dispatch={dispatch}
                    likeLoading={likeLoading}
                    followingsMap={followingsMap}    
                    followLoading={followLoading}
                    />   
                </InfiniteScroll>
            </Tabs.TabPane>

            {user && <Tabs.TabPane  tab="좋아요 한 글"  key="liked">
                  <PostList
                    posts={likedPosts}
                    user={user}
                    likes={likes}
                    likesCount={likesCount}
                    retweetedPosts={retweets}
                    retweetsCount={retweetsCount}
                    expandedPostId={expandedPostId}
                    setExpandedPostId={setExpandedPostId}
                    handleToggleLike={handleToggleLike}
                    handleToggleFollow={handleToggleFollow}
                    handleEdit={handleEdit}
                    dispatch={dispatch}
                    likeLoading={likeLoading}
                    followingsMap={followingsMap}    
                    followLoading={followLoading}
                    />   
            </Tabs.TabPane> } 

            {user && <Tabs.TabPane  tab="내 글 + 리트윗"  key="my">
                  <PostList
                    posts={myAndRetweets}
                    user={user}
                    likes={likes}
                    likesCount={likesCount}
                    retweetedPosts={retweets}
                    retweetsCount={retweetsCount}
                    expandedPostId={expandedPostId}
                    setExpandedPostId={setExpandedPostId}
                    handleToggleLike={handleToggleLike}
                    handleToggleFollow={handleToggleFollow}
                    handleEdit={handleEdit}
                    dispatch={dispatch}
                    likeLoading={likeLoading}
                    followingsMap={followingsMap}    
                    followLoading={followLoading}
                    />   
            </Tabs.TabPane> } 
        </Tabs>
        <EditPostModal
            visible={isEditModalVisible}
            editPost={editPost}
            onCancel={() => setIsEditModalVisible(false)}
            onSubmit={handleEditSubmit}
            uploadFiles={uploadFiles}
            setUploadFiles={setUploadFiles}
        />       
    </>;
}

// export default function Home(){
//     return <h1>hello</h1>;
// }
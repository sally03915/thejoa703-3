// tests/likeSaga.test.js
import { runSaga } from "redux-saga";
import * as api from "../../api/axios";  
import {
  addLikeSaga, removeLikeSaga, countLikesSaga, fetchMyLikesSaga,
} from "../likeSaga";
import {
  addLikeSuccess, addLikeFailure,
  removeLikeSuccess, removeLikeFailure,
  countLikesSuccess, countLikesFailure,
  fetchMyLikesSuccess, fetchMyLikesFailure,
} from "../../reducers/likeReducer";

jest.mock("../../api/axios");

describe("likeSaga", () => {
  it("addLikeSuccess", async () => {
    const dispatched = [];
    api.default.post.mockResolvedValue({ data: { postId: 1, count: 5 } });

    await runSaga({
      dispatch: (action) => dispatched.push(action),
    }, addLikeSaga, { payload: { postId: 1 } }).toPromise();   // { payload: { postId: 1 } }  addLikeRequest

    expect(dispatched).toContainEqual(addLikeSuccess({ postId: 1, count: 5 }));
  });

  it("removeLikeSuccess", async () => {
    const dispatched = [];
    api.default.delete.mockResolvedValue({ data: { postId: 1, count: 4 } });
    await runSaga({
      dispatch: (action) => dispatched.push(action),
    }, removeLikeSaga, { payload: { postId: 1 } }).toPromise();

    expect(dispatched).toContainEqual(removeLikeSuccess({ postId: 1, count: 4 }));
  });

  it("countLikesSuccess", async () => {
    const dispatched = [];
    api.default.get.mockResolvedValue({ data: { postId: 2, count: 10 } });

    await runSaga({
      dispatch: (action) => dispatched.push(action),
    }, countLikesSaga, { payload: { postId: 2 } }).toPromise();

    expect(dispatched).toContainEqual(countLikesSuccess({ postId: 2, count: 10 }));
  });

  it("fetchMyLikesSuccess", async () => {
    const dispatched = [];
    api.default.get.mockResolvedValue({ data: [1, 2, 3] });
    
    await runSaga({
      dispatch: (action) => dispatched.push(action),
    }, fetchMyLikesSaga, { payload: { userId: 1 } }).toPromise();

    expect(dispatched).toContainEqual(fetchMyLikesSuccess([1, 2, 3]));
  });

  it("addLikeFailure", async () => {
    const dispatched = [];
    api.default.post.mockRejectedValue(new Error("fail"));
    
    await runSaga({
      dispatch: (action) => dispatched.push(action),
    }, addLikeSaga, { payload: { postId: 1 } }).toPromise();

    expect(dispatched[0].type).toBe(addLikeFailure("").type);
  });
});

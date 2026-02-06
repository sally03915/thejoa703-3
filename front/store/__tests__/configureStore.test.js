// __tests__/configureStore.test.js
import { makeStore } from '../../store/configureStore';
import { loginSuccess, logout } from '../../reducers/authReducer';
describe('store configuration', () => {
  it('dispatches actions and updates state', () => {
    const store = makeStore();
    store.dispatch(loginSuccess({ user: { email: 'test@test' }, accessToken: null }));

    expect(store.getState().auth.user.email).toBe('test@test');
    expect(store.getState().auth.accessToken).toBeNull(); 

    store.dispatch(logout());
    expect(store.getState().auth.user).toBeNull();

  });
});

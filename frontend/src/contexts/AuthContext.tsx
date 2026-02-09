/**
 * 認証コンテキスト
 * ログイン状態の保持・ログイン/ログアウト処理を提供する
 */

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import { login as loginApi } from '../api/authApi';
import { clearStoredToken, getStoredToken, setStoredToken } from '../infrastructure/api/client';
import type { AuthResponse } from '../types/api';

interface AuthState {
  /** ログイン済みユーザー情報（未ログイン時は null） */
  user: AuthResponse | null;
  /** 初期読み込み中（トークン有無の確認） */
  loading: boolean;
}

interface AuthContextValue extends AuthState {
  /** ログイン処理。成功時はユーザー情報を返す */
  login: (username: string, password: string) => Promise<AuthResponse>;
  /** 認証レスポンスで状態を更新（会員登録成功時など、APIを再度叩かずにログイン状態にする） */
  setAuthFromResponse: (response: AuthResponse) => void;
  /** ログアウト（トークン削除と状態クリア） */
  logout: () => void;
  /** ログイン済みか */
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

interface AuthProviderProps {
  children: ReactNode;
}

/**
 * 認証プロバイダ
 * アプリ全体で認証状態を共有する
 */
export function AuthProvider({ children }: AuthProviderProps) {
  const [state, setState] = useState<AuthState>({
    user: null,
    loading: true,
  });

  /** トークンからはユーザー情報を復元できないため、ログイン時のみ user を保持。リロード時はトークンの有無で isAuthenticated を判定 */
  useEffect(() => {
    const token = getStoredToken();
    if (!token) {
      setState((s) => ({ ...s, user: null, loading: false }));
      return;
    }
    setState((s) => ({ ...s, loading: false }));
    // オプション: トークン検証APIがあればここでユーザー情報を再取得できる
  }, []);

  const login = useCallback(async (username: string, password: string) => {
    const res = await loginApi({ username, password });
    setStoredToken(res.token);
    setState({ user: res, loading: false });
    return res;
  }, []);

  const setAuthFromResponse = useCallback((response: AuthResponse) => {
    setStoredToken(response.token);
    setState({ user: response, loading: false });
  }, []);

  const logout = useCallback(() => {
    clearStoredToken();
    setState({ user: null, loading: false });
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      ...state,
      login,
      setAuthFromResponse,
      logout,
      isAuthenticated: !!getStoredToken() || !!state.user,
    }),
    [state, login, setAuthFromResponse, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

/**
 * 認証コンテキストを取得する
 * AuthProvider の外で使用すると throw する
 */
export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return ctx;
}

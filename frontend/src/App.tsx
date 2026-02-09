/**
 * 推し活フロントエンド ルートコンポーネント
 * ルーティングと認証プロバイダを設定する
 */

import { Navigate, Route, BrowserRouter, Routes } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { ProtectedRoute } from './presentation/components/ProtectedRoute';
import { AppLayout } from './presentation/components/AppLayout';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { OshiGroupPage } from './presentation/pages/oshiGroup/OshiGroupPage';
import { OshiMemberPage } from './presentation/pages/oshiMember/OshiMemberPage';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route index element={<DashboardPage />} />
            <Route path="oshi-groups" element={<OshiGroupPage />} />
            <Route path="oshi-members" element={<OshiMemberPage />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;

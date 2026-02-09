/**
 * 認証後のアプリレイアウト
 * サイドナビとメインコンテンツ領域を提供する
 */

import { Link, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import './AppLayout.css';

export function AppLayout() {
  const { user, logout } = useAuth();
  const location = useLocation();

  const navItems = [
    { to: '/', label: 'ダッシュボード' },
    { to: '/oshi-groups', label: '推しグループ' },
    { to: '/oshi-members', label: '推しメンバー' },
  ];

  return (
    <div className="app-layout">
      <aside className="app-layout__sidebar">
        <div className="app-layout__brand">
          <Link to="/">推し活</Link>
        </div>
        <nav className="app-layout__nav" aria-label="メインメニュー">
          <ul>
            {navItems.map(({ to, label }) => (
              <li key={to}>
                <Link
                  to={to}
                  className={location.pathname === to ? 'active' : ''}
                >
                  {label}
                </Link>
              </li>
            ))}
          </ul>
        </nav>
        <div className="app-layout__user">
          {user && (
            <span className="app-layout__username" title={user.email}>
              {user.username}
            </span>
          )}
          <button
            type="button"
            className="btn-secondary app-layout__logout"
            onClick={logout}
          >
            ログアウト
          </button>
        </div>
      </aside>
      <main className="app-layout__main">
        <Outlet />
      </main>
    </div>
  );
}

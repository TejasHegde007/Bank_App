import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Skip attaching Authorization header for the token endpoint itself
  if (req.method === 'POST' && req.url.includes('/protocol/openid-connect/token')) {
    return next(req);
  }
  // Also skip public user endpoints (no auth required)
  if (req.url.includes('/api/users/public/')) {
    return next(req);
  }

  const isBrowser = typeof window !== 'undefined' && typeof window.document !== 'undefined';
  let token: string | null = null;
  if (isBrowser) {
    try {
      token = localStorage.getItem('access_token');
    } catch {
      token = null;
    }
  }
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};

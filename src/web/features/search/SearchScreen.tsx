import React, { useState, useEffect } from 'react';
import { Typography } from '../../components/atoms/Typography';
import { Icon } from '../../components/atoms/Icon';
import { getAuth } from 'firebase/auth';
import { getDatabase, ref, onValue } from 'firebase/database';

export const SearchScreen: React.FC = () => {
  const auth = getAuth();
  const user = auth.currentUser;
  const userInitial = user?.displayName ? user.displayName[0].toUpperCase() : (user?.email ? user.email[0].toUpperCase() : 'M');

  const [categories, setCategories] = useState<any[]>([]);
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const db = getDatabase();
    const categoriesRef = ref(db, 'content/categories');
    
    const unsubscribe = onValue(categoriesRef, (snapshot) => {
      const data = snapshot.val();
      if (data) {
        const cats = Array.isArray(data) ? data : Object.keys(data).map(key => ({ id: key, ...data[key] }));
        setCategories(cats);
      }
    }, (error) => {
      console.error('Error fetching categories from RTDB:', error);
    });

    return () => unsubscribe();
  }, []);

  return (
    <div className="flex flex-col h-full w-full bg-background overflow-y-auto pb-32">
      <div className="px-4 pt-12 pb-2 sticky top-0 bg-background/90 backdrop-blur-md z-10 flex items-center gap-3">
        <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-xs font-bold text-black overflow-hidden shadow-inner flex-shrink-0">
          {userInitial}
        </div>
        <Typography variant="title-lg" className="font-bold flex-1">Search</Typography>
        <Icon name="camera_alt" size="xl" className="cursor-pointer" onClick={() => console.log('Camera clicked')} />
      </div>

      <div className="px-4 sticky top-[72px] bg-background/90 backdrop-blur-md z-10 pb-4 border-b border-surface-container">
        <div className="bg-white rounded-[4px] flex items-center p-2.5 gap-2">
          <Icon name="search" color="secondary" size="md" className="text-black" />
          <input 
            type="text" 
            placeholder="What do you want to listen to?" 
            className="flex-1 bg-transparent border-none outline-none text-black font-medium placeholder-gray-500"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      <div className="px-4 py-4">
        <Typography variant="title-md" className="font-bold mb-4">Browse all</Typography>
        <div className="grid grid-cols-2 gap-4">
          {categories.map((cat) => (
            <div 
              key={cat.id} 
              style={{ backgroundColor: cat.colorHex || cat.color || '#777777' }}
              className={`aspect-[1.5] rounded-[4px] p-3 relative overflow-hidden shadow-md cursor-pointer`}
              onClick={() => console.log('Category clicked', cat.id)}
            >
              <Typography variant="title-md" className={`font-bold text-white z-10 relative break-words`}>
                {cat.title}
              </Typography>
              <div className="absolute -bottom-2 -right-4 w-16 h-16 bg-black/20 rounded-[4px] transform rotate-[25deg] shadow-lg"></div>
            </div>
          ))}
          {categories.length === 0 && (
             <div className="text-sm text-text-secondary italic col-span-2">Loading categories...</div>
          )}
        </div>
      </div>
    </div>
  );
};

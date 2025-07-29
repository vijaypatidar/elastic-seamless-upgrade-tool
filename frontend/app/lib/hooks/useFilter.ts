import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router';

// Define a flexible type for filters
type FilterValue = string | boolean | string[];
type Filters = Record<string, FilterValue>;

function useFilters<T extends Filters>(initialFilters: T): [T, (key: keyof T, value: FilterValue) => void] {
    const location = useLocation();
    const navigate = useNavigate();

    // Parse filters from the URL
    const getFiltersFromUrl = (): Partial<T> => {
        const params = new URLSearchParams(location.search);
        const filters: Partial<T> = {};

        for (const [key, value] of params.entries()) {
            if (value === 'true' || value === 'false') {
                filters[key as keyof T] = value === 'true';
            } else if (value.startsWith('[') && value.endsWith(']')) {
                try {
                    filters[key as keyof T] = JSON.parse(value);
                } catch {
                    filters[key as keyof T] = [] as unknown as T[keyof T];
                }
            } else {
                filters[key as keyof T] = value as unknown as T[keyof T];
            }
        }

        return filters;
    };

    const [filters, setFilters] = useState<T>(() => {
        const urlFilters = getFiltersFromUrl();
        return { ...initialFilters, ...urlFilters };
    });

    // Sync filters with URL
    useEffect(() => {
        const params = new URLSearchParams();

        Object.entries(filters).forEach(([key, value]) => {
            if (
                value !== null &&
                value !== '' &&
                (Array.isArray(value) ? value.length > 0 : true) &&
                value !== false
            ) {
                if (Array.isArray(value)) {
                    params.set(key, JSON.stringify(value));
                } else {
                    params.set(key, value.toString());
                }
            }
        });

        navigate({ search: params.toString() }, { replace: true });
    }, [filters, navigate]);

    // Method to update individual filter
    const updateFilter = (key: keyof T, value: FilterValue) => {
        setFilters((prev) => ({
            ...prev,
            [key]: value as T[keyof T],
        }));
    };

    return [filters, updateFilter];
}

export default useFilters;
